package store.mybooks.authorization.jwt.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

        String authority = "ROLE_ADMIN";
        String status = "ACTIVE";
        String uuid = "mockUuid";

        when(request.getIsAdmin()).thenReturn(true);
        when(request.getStatus()).thenReturn("ACTIVE");
        when(request.getUuid()).thenReturn("mockUuid");

        when(jwtConfig.getIssuer()).thenReturn("mockIssuer");
        when(jwtConfig.getAccessExpiration()).thenReturn(3600L);
        when(jwtConfig.getSecret()).thenReturn("mockSecret");
        when(keyConfig.keyStore("mockSecret")).thenReturn("mockKeyStore");

        String accessToken = tokenService.createAccessToken(request);


        DecodedJWT jwt = JWT.decode(accessToken);
        assertEquals("mockIssuer", jwt.getIssuer());
        assertEquals(uuid, jwt.getSubject());
        assertEquals(authority, jwt.getClaim("authority").asString());
        assertEquals(status, jwt.getClaim("status").asString());

        verify(request, times(1)).getStatus();
        verify(request, times(1)).getUuid();
        verify(request, times(1)).getIsAdmin();
    }

    @Test
    @DisplayName("기존 엑세스 토큰을 디코딩한 DecodedJwt 로 refreshAccessToken 실행시 동작 테스트")
    void givenDecodedJwt_whenCallRefreshAccessToken_thenReturnToken() {

        String subject = "mockSubject";
        String authority = "ROLE_USER";
        String status = "ACTIVE";
        Date issuedAt = new Date();

        String token = JWT.create()
                .withSubject(subject)
                .withClaim("authority", authority)
                .withClaim("status", status)
                .withIssuedAt(issuedAt)
                .sign(Algorithm.HMAC512("mockSecret")); // Mocking secret key

        when(jwtConfig.getIssuer()).thenReturn("mockIssuer");
        when(jwtConfig.getAccessExpiration()).thenReturn(3600L); // 1 hour expiration
        when(jwtConfig.getSecret()).thenReturn("mockSecret");
        when(keyConfig.keyStore("mockSecret")).thenReturn("mockKeyStore");

        String newAccessToken = tokenService.refreshAccessToken(JWT.decode(token));

        DecodedJWT newJwt = JWT.decode(newAccessToken);
        assertEquals(subject, newJwt.getSubject());
        assertEquals(authority, newJwt.getClaim("authority").asString());
        assertEquals(status, newJwt.getClaim("status").asString());
        assertEquals("mockIssuer", newJwt.getIssuer());

    }
}