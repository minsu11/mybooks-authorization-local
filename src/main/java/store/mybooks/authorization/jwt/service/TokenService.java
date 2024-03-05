package store.mybooks.authorization.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import java.io.OutputStream;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final JwtConfig jwtConfig;

    private final KeyConfig keyConfig;

    // todo 토큰 발행 Request 필요 (사용자 아이디 , 권한)
    // todo 엑세스 토큰 , 리프레시 토큰 담을 Response 필요 이걸 Front 한테 던져주면 쿠키에 엑세스 토큰 등록하고 , 매 요청마다 해더에 던져줄꺼임
    // todo 게이트웨이에서 request 에 대한 헤더를 까서 jwt 검증하고 , 권한 체크해서 디나이 시키든 먹이든 한다
    public String createAccessToken(TokenRequest tokenRequest) {

        String role;


        if (tokenRequest.getIsAdmin()) {
            role = ROLE_ADMIN;
        } else {
            role = ROLE_USER;
        }


        Date issuedAt = new Date(System.currentTimeMillis());

        return JWT.create()
                .withIssuer(jwtConfig.getIssuer())
                .withSubject(String.valueOf(tokenRequest.getUserId())) // 토큰이름 , 이걸로 사용자 식별 todo 이거 변경필요
                .withIssuedAt(issuedAt) // 발행일
                .withExpiresAt(new Date(issuedAt.getTime() + jwtConfig.getExpiration())) // 토큰만료일
                .withClaim("authority", role) // 회원 권한
                .withClaim("status", tokenRequest.getStatus()) // 회원상태
                .sign(Algorithm.HMAC512(keyConfig.keyStore(jwtConfig.getSecret()))); // 시크릿은 key manager 로 관리
    }

    public String createRefreshToken(String accessToken) {

        return JWT.create()
                .withSubject("1") // todo 리프레시토큰 로직 변경 필요
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtConfig.getExpiration())) // 토큰만료일
                .withClaim("AccessToken", accessToken) // AccessToken
                .sign(Algorithm.HMAC512(jwtConfig.getSecret())); // 해쉬로 암호처리
    }

}
