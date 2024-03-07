package store.mybooks.authorization.jwt.controller;

import java.time.Duration;
import java.util.Enumeration;
import java.util.Objects;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.mybooks.authorization.config.JwtConfig;
import store.mybooks.authorization.config.KeyConfig;
import store.mybooks.authorization.config.RedisConfig;
import store.mybooks.authorization.jwt.dto.request.RefreshTokenRequest;
import store.mybooks.authorization.jwt.dto.request.TokenRequest;
import store.mybooks.authorization.jwt.dto.response.RefreshTokenResponse;
import store.mybooks.authorization.jwt.dto.response.TokenResponse;
import store.mybooks.authorization.jwt.service.TokenService;
import store.mybooks.authorization.redis.RedisService;

/**
 * packageName    : store.mybooks.authorization.auth.controller<br>
 * fileName       : AuthRestController<br>
 * author         : masiljangajji<br>
 * date           : 2/28/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2/28/24        masiljangajji       최초 생성
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class TokenRestController {

    private final TokenService authService;

    private final RedisService redisService;

    private final JwtConfig jwtConfig;

    private final RedisConfig redisConfig;

    private final KeyConfig keyConfig;


    @PostMapping
    public ResponseEntity<TokenResponse> createToken(
            @RequestBody TokenRequest tokenRequest, HttpServletRequest request) {

        String accessToken = authService.createAccessToken(tokenRequest);

        System.out.println("!!!!");
        System.out.println(request.getRemoteAddr());
        //  Key {ip주소} - Value {리프래시토큰}

        // 키메니저에서 설정한 비밀 값 + 엑세스 토큰 + 유저 ip 주소
        // 엑세스 토큰은 ip주소가 우연히 같을 수 있기 떄문에 넣어줌

        redisService.setValues(keyConfig.keyStore(redisConfig.getRedisKey()) + accessToken + request.getRemoteAddr(),
                keyConfig.keyStore(redisConfig.getRedisValue()),
                Duration.ofMillis(jwtConfig.getRefreshExpiration()));

        return new ResponseEntity<>(new TokenResponse(accessToken), HttpStatus.CREATED);
    }




}
