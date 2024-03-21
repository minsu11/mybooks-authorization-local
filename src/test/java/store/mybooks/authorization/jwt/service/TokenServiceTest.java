package store.mybooks.authorization.jwt.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import store.mybooks.authorization.config.JwtConfig;
import store.mybooks.authorization.config.KeyConfig;
import store.mybooks.authorization.jwt.dto.request.TokenRequest;

/**
 * packageName    : store.mybooks.authorization.jwt.service<br>
 * fileName       : TokenServiceTest<br>
 * author         : masiljangajji<br>
 * date           : 3/20/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/20/24        masiljangajji       최초 생성
 */

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {


    @InjectMocks
    TokenService tokenService;

    @Mock
    JwtConfig jwtConfig;

    @Mock
    KeyConfig keyConfig;


    @Test
    @DisplayName("TokenRequest 로 CreateAccessToken 실행시 동작 테스트")
    void givenTokenRequest_whenCallCreateAccessToken_thenReturnToken(@Mock TokenRequest request) {


        when(request.getIsAdmin()).thenReturn(true);
        when(request.getStatus()).thenReturn("TEST");
        when(request.getUuid()).thenReturn("testUUID");

        when(jwtConfig.getIssuer()).thenReturn("my-books");
        when(jwtConfig.getSecret()).thenReturn("secret");
        when(jwtConfig.getAccessExpiration()).thenReturn(1800000L);
        when(keyConfig.keyStore(anyString())).thenReturn("key-secret");

        String token = tokenService.createAccessToken(request);

        verify(request, times(1)).getStatus();
        verify(request, times(1)).getUuid();
        verify(request, times(1)).getIsAdmin();


        Algorithm algorithm = Algorithm.HMAC512(keyConfig.keyStore(jwtConfig.getSecret()));
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();

        DecodedJWT jwt = jwtVerifier.verify(token);

        assertEquals(token, jwt.getToken());
        assertEquals("testUUID", jwt.getSubject());
        assertEquals("my-books", jwt.getIssuer());
        assertEquals("ROLE_ADMIN", jwt.getClaim("authority").asString());
    }

    @Test
    @DisplayName("기존 엑세스 토큰을 디코딩한 DecodedJwt 로 refreshAccessToken 실행시 동작 테스트")
    void givenDecodedJwt_whenCallRefreshAccessToken_thenReturnToken(@Mock DecodedJWT jwt, @Mock Claim authClaim, @Mock Claim statusClaim) {


        when(jwt.getClaim("authority")).thenReturn(authClaim);
        when(jwt.getClaim("status")).thenReturn(statusClaim);
        when(jwtConfig.getIssuer()).thenReturn("my-books");
        when(jwtConfig.getSecret()).thenReturn("secret");
        when(jwtConfig.getAccessExpiration()).thenReturn(1800000L);
        when(keyConfig.keyStore(anyString())).thenReturn("key-secret");

        tokenService.refreshAccessToken(jwt);

        verify(jwtConfig, times(1)).getIssuer();
        verify(jwtConfig, times(1)).getSecret();
        verify(jwtConfig,times(1)).getAccessExpiration();
        verify(jwt, times(1)).getClaim("authority");
        verify(jwt, times(1)).getClaim("status");


    }
}