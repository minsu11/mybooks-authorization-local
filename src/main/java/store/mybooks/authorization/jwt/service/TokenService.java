package store.mybooks.authorization.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.mybooks.authorization.config.JwtConfig;
import store.mybooks.authorization.config.KeyConfig;
import store.mybooks.authorization.jwt.dto.request.TokenRequest;

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

    private static final String AUTHORITY = "authority";
    private static final String STATUS = "status";

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
                .withSubject(String.valueOf(tokenRequest.getUuid()))
                .withIssuedAt(issuedAt)
                .withExpiresAt(new Date(issuedAt.getTime() + jwtConfig.getAccessExpiration()))
                .withClaim(AUTHORITY, authority)
                .withClaim(STATUS, tokenRequest.getStatus())
                .sign(Algorithm.HMAC512(keyConfig.keyStore(jwtConfig.getSecret())));
    }

    public String refreshAccessToken(DecodedJWT jwt) {


        Date issuedAt = new Date(System.currentTimeMillis());

        String authority = jwt.getClaim(AUTHORITY).asString();
        String status = jwt.getClaim(STATUS).asString();


        return JWT.create()
                .withIssuer(jwtConfig.getIssuer())
                .withSubject(jwt.getSubject())
                .withIssuedAt(issuedAt)
                .withExpiresAt(new Date(issuedAt.getTime() + jwtConfig.getAccessExpiration()))
                .withClaim(AUTHORITY, authority)
                .withClaim(STATUS, status)
                .sign(Algorithm.HMAC512(keyConfig.keyStore(jwtConfig.getSecret())));
    }

}
