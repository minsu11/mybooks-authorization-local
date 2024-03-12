package store.mybooks.authorization.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import store.mybooks.authorization.config.JwtConfig;
import store.mybooks.authorization.config.KeyConfig;
import store.mybooks.authorization.jwt.dto.request.RefreshTokenRequest;
import store.mybooks.authorization.jwt.dto.request.TokenRequest;
import store.mybooks.authorization.redis.RedisService;

/**
 * packageName    : store.mybooks.authorization.auth.service<br>
 * fileName       : AuthService<br>
 * author         : masiljangajji<br>
 * date           : 2/28/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2/28/24        masiljangajji       최초 생성
 */
@Service
@RequiredArgsConstructor
public class TokenService {
    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";

    private final JwtConfig jwtConfig;

    private final KeyConfig keyConfig;

    public String createAccessToken(TokenRequest tokenRequest) {

        String authority;

        if (tokenRequest.getIsAdmin()) {
            authority = ROLE_ADMIN;
        } else {
            authority = ROLE_USER;
        }

        Date issuedAt = new Date(System.currentTimeMillis());


        return JWT.create()
                .withIssuer(jwtConfig.getIssuer())
                .withSubject(String.valueOf(tokenRequest.getUuid())) // 토큰이름
                .withIssuedAt(issuedAt) // 발행일
                .withExpiresAt(new Date(issuedAt.getTime() + jwtConfig.getAccessExpiration())) // 토큰만료일
                .withClaim("authority", authority) // 회원 권한
                .withClaim("status", tokenRequest.getStatus()) // 회원상태
                .sign(Algorithm.HMAC512(keyConfig.keyStore(jwtConfig.getSecret()))); // 시크릿은 key manager 로 관리
    }

    public String refreshAccessToken(DecodedJWT jwt) {


        Date issuedAt = new Date(System.currentTimeMillis());

        String authority= String.valueOf(jwt.getClaim("authority"));
        String status = String.valueOf(jwt.getClaim("status"));

        authority= authority.replaceAll("\"","");
        status=status.replaceAll("\"","");

        return JWT.create()
                .withIssuer(jwtConfig.getIssuer())
                .withSubject(jwt.getSubject())
                .withIssuedAt(issuedAt)
                .withExpiresAt(new Date(issuedAt.getTime() + jwtConfig.getAccessExpiration())) // 토큰만료일
                .withClaim("authority", authority) // 회원 권한
                .withClaim("status", status) // 회원상태
                .sign(Algorithm.HMAC512(keyConfig.keyStore(jwtConfig.getSecret()))); // 시크릿은 key manager 로 관리
    }

}
