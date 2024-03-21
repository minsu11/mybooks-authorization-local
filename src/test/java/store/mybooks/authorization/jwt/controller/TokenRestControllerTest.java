package store.mybooks.authorization.jwt.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import store.mybooks.authorization.config.JwtConfig;
import store.mybooks.authorization.config.KeyConfig;
import store.mybooks.authorization.config.RedisConfig;
import store.mybooks.authorization.jwt.dto.request.LogoutRequest;
import store.mybooks.authorization.jwt.dto.request.RefreshTokenRequest;
import store.mybooks.authorization.jwt.dto.request.TokenRequest;
import store.mybooks.authorization.jwt.service.TokenService;
import store.mybooks.authorization.redis.RedisService;

/**
 * packageName    : store.mybooks.authorization.jwt.controller<br>
 * fileName       : TokenRestControllerTest<br>
 * author         : masiljangajji<br>
 * date           : 3/20/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/20/24        masiljangajji       최초 생성
 */

@WebMvcTest(value = TokenRestController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {
        "jwt.access.expiration=3600",
        "jwt.refresh.expiration=7200"
})
class TokenRestControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TokenService tokenService;

    @MockBean
    RedisService redisService;

    @MockBean
    RedisConfig redisConfig;

    @MockBean
    KeyConfig keyConfig;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean
    JwtConfig jwtConfig;
    String ACCESS_TOKEN =
            "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJteS1ib29rcy5jb20iLCJzdWIiOiI4NzY1Y2YwNS00NDFkLTRjZTYtOTQ0ZC05MzRkYjY5NzM5MjkiLCJpYXQiOjE3MTA5Nzk3NjcsImV4cCI6MTcxMDk4MTU2NywiYXV0aG9yaXR5IjoiUk9MRV9BRE1JTiIsInN0YXR1cyI6Iu2ZnOyEsSJ9.swDjcgndPCPiu2ky93WHjKP995R5th4rbPFNdBA0hIpyFsV4jIJ8Tq3Zn442A74-DKviTLqW1yffnB-VyUDJYw";

    RefreshTokenRequest request = new RefreshTokenRequest(ACCESS_TOKEN, "ip", "userAgent");


    @Test
    @DisplayName("TokenRequest 로 createToken 실행시 TokenResponse 반환")
    void givenTokenRequest_whenCallCreateToken_thenReturnTokenResponse() throws Exception {


        when(tokenService.createAccessToken(any(TokenRequest.class))).thenReturn("access-token");
        String requestBody = "{\"uuid\": \"test\", \"ip\": \"127.0.0.1\", \"userAgent\": \"Mozilla/5.0\", \"userId\": 123}";

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").exists());

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Duration> durationCaptor = ArgumentCaptor.forClass(Duration.class);
        verify(redisService, times(2)).setValues(keyCaptor.capture(), valueCaptor.capture(), durationCaptor.capture());

        assertEquals("test127.0.0.1Mozilla/5.0", keyCaptor.getAllValues().get(0));
        assertEquals("123", valueCaptor.getAllValues().get(0));
    }

    @Test
    @DisplayName("RefreshTokenRequest로 refreshAccessToken 실행시 TokenResponse 반환")
    void givenRefreshTokenRequest_whenCallRefreshAccessToken_thenReturnTokenResponse() throws Exception {
        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(ACCESS_TOKEN,"127.0.0.1","Mozilla/5.0");


        DecodedJWT jwt = JWT.decode(refreshTokenRequest.getAccessToken());
        String newAccessToken = "new-access-token";
        when(tokenService.refreshAccessToken(any(DecodedJWT.class))).thenReturn(newAccessToken);

        String ipAddress = refreshTokenRequest.getIp();
        String userAgent = refreshTokenRequest.getUserAgent();
        String refreshToken = "refresh-token";
        when(redisService.getValues(refreshTokenRequest.getAccessToken() + ipAddress + userAgent)).thenReturn(refreshToken);

        String key = keyConfig.keyStore(redisConfig.getRedisValue()) + ipAddress;
        when(passwordEncoder.matches(eq(key), eq(refreshToken))).thenReturn(true);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value(newAccessToken));

        verify(redisService).getValues(refreshTokenRequest.getAccessToken() + ipAddress + userAgent);
        verify(redisService).deleteValues(refreshTokenRequest.getAccessToken() + ipAddress + userAgent);
        verify(redisService).expireValues(eq(jwt.getSubject() + ipAddress + userAgent), anyLong());
    }

    @Test
    @DisplayName("RefreshTokenRequest 로 refreshAccessToken 실행시 TokenResponse 반환 (Refresh Token 만료되어 redis 에 정보 없음)")
    void givenRefreshTokenRequest_whenCallRefreshAccessTokenAndRefreshTokenExpired_thenReturnRefreshTokenResponseWithNull()
            throws Exception {

        when(redisService.getValues(anyString())).thenReturn(null);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").doesNotExist());
    }

    @Test
    @DisplayName("RefreshTokenRequest 로 refreshAccessToken 실행시 TokenResponse 반환 (Refresh Token 변조되어 passwordEncoder 매치안됨)")
    void givenNotValidRefreshTokenRequest_whenCallRefreshAccessToken_thenReturnRefreshTokenResponse()
            throws Exception {

        when(redisService.getValues(anyString())).thenReturn("access-token");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").doesNotExist());
    }


    @Test
    @DisplayName("LogoutRequest 로 deleteRefreshToken 실행시 NO CONTENT")
    void deleteRefreshToken() throws Exception {

        LogoutRequest logoutRequest = new LogoutRequest(ACCESS_TOKEN,"127.0.0.1","Mozilla/5.0");

        doNothing().when(redisService).deleteValues(anyString());

        mockMvc.perform(delete("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isNoContent());

        verify(redisService).deleteValues(ACCESS_TOKEN+"127.0.0.1Mozilla/5.0");
        verify(redisService).deleteValues("8765cf05-441d-4ce6-944d-934db6973929127.0.0.1Mozilla/5.0");
    }




}