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
import org.springframework.stereotype.Service;
import store.mybooks.authorization.config.JwtConfig;
import store.mybooks.authorization.config.KeyConfig;
import store.mybooks.authorization.jwt.dto.request.RefreshTokenRequest;
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

    private final JwtConfig jwtConfig;

    private final KeyConfig keyConfig;

    // todo 토큰 발행 Request 필요 (사용자 아이디 , 권한)
    // todo 엑세스 토큰 , 리프레시 토큰 담을 Response 필요 이걸 Front 한테 던져주면 쿠키에 엑세스 토큰 등록하고 , 매 요청마다 해더에 던져줄꺼임
    // todo 게이트웨이에서 request 에 대한 헤더를 까서 jwt 검증하고 , 권한 체크해서 디나이 시키든 먹이든 한다
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
                .withSubject(String.valueOf(tokenRequest.getUserId())) // 토큰이름 , 이걸로 사용자 식별 todo 이거 변경필요
                .withIssuedAt(issuedAt) // 발행일
                .withExpiresAt(new Date(issuedAt.getTime() + jwtConfig.getAccessExpiration())) // 토큰만료일
                .withClaim("authority", authority) // 회원 권한
                .withClaim("status", tokenRequest.getStatus()) // 회원상태
                .sign(Algorithm.HMAC512(keyConfig.keyStore(jwtConfig.getSecret()))); // 시크릿은 key manager 로 관리
    }

    public String refreshAccessToken(RefreshTokenRequest refreshTokenRequest) {

        DecodedJWT jwt = JWT.decode(refreshTokenRequest.getAccessToken());

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
