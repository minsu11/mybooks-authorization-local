package store.mybooks.authorization.jwt.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Duration;
import java.util.Enumeration;
import java.util.Objects;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.mybooks.authorization.config.JwtConfig;
import store.mybooks.authorization.config.KeyConfig;
import store.mybooks.authorization.config.RedisConfig;
import store.mybooks.authorization.jwt.dto.request.LogoutRequest;
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

    private final PasswordEncoder passwordEncoder;


    @PostMapping
    public ResponseEntity<TokenResponse> createToken(
            @RequestBody TokenRequest tokenRequest, HttpServletRequest request) {

        // UUID+ip주소가 키 , value에 유저아이디
        redisService.setValues(tokenRequest.getUuid() + request.getRemoteAddr(),
                String.valueOf(tokenRequest.getUserId()),
                Duration.ofMillis(jwtConfig.getRefreshExpiration()));

        String accessToken = authService.createAccessToken(tokenRequest);

        // Key = 엑세스 토큰 + ip 주소
        // Value = (키메너지가 관리하는 암호 + ip주소) 를 BCrypt 로 잠근 값
        // 이것에 대해서 만료시간을 줌 , 일정시간이 지나면 자동으로 삭제되게 만듦
        // 재발급시 엑세스 토큰 + ip주소로 찾고 ,  Value를 키메너지가 관리하는 암호 + ip주소 로 matches 로 검증함

        // 만약 레디스 서버가 털리더라도 키메니저가 관리하는 암호를 모르기 떄문에 mybooks가 발급하지 않은 Value를 집어넣으면 matches 에서 실패하게 됨
        // 따라서 리프래시토큰에 대한 변조를 막을 수 있고 , 만료시간이 지나면 자동으로 사라지기 떄문에 관리가 가능 함
        redisService.setValues(accessToken + request.getRemoteAddr(),
                passwordEncoder.encode(keyConfig.keyStore(redisConfig.getRedisValue()) + request.getRemoteAddr()),
                Duration.ofMillis(jwtConfig.getRefreshExpiration()));

        return new ResponseEntity<>(new TokenResponse(accessToken), HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest,
                                                                   HttpServletRequest request) {

        String key = refreshTokenRequest.getAccessToken() + request.getRemoteAddr();
        String refreshToken = redisService.getValues(key);


        // 키 = 엑세스토큰 + ip주소
        // 벨류 = 암호화한 값 + ip주소
        if (Objects.isNull(refreshToken) ||
                !passwordEncoder.matches(keyConfig.keyStore(redisConfig.getRedisValue()) + request.getRemoteAddr(),
                        refreshToken)) { // null 이면 만료된 것 , BCrypt 로 잠근 값을 매치로 확인해 , 내가 넣어준 유효한 리프래시 토큰인지 검증
            return new ResponseEntity<>(new RefreshTokenResponse(false, null), HttpStatus.NO_CONTENT);
        }

        DecodedJWT jwt = JWT.decode(refreshTokenRequest.getAccessToken());

        String newAccessToken = authService.refreshAccessToken(jwt); // 엑세스토큰 새로 만들고
        redisService.deleteValues(key); // 기존에 있던 리프래시 토큰을 삭제

        // 리프래시토큰 갱신
        redisService.setValues(newAccessToken + request.getRemoteAddr(),
                passwordEncoder.encode(keyConfig.keyStore(redisConfig.getRedisValue()) + request.getRemoteAddr()),
                Duration.ofMillis(jwtConfig.getRefreshExpiration()));

        // 유저 아이디를 담아놓은 레디스 갱신
        redisService.expireValues(jwt.getSubject() + request.getRemoteAddr(), jwtConfig.getRefreshExpiration());

        // 새로운 엑세스 토큰을 발행 함
        return new ResponseEntity<>(new RefreshTokenResponse(true, newAccessToken), HttpStatus.CREATED);
    }


    @DeleteMapping("/logout")
    public ResponseEntity<Void> deleteRefreshToken(@RequestBody LogoutRequest logoutRequest,
                                                   HttpServletRequest request) {
        DecodedJWT jwt = JWT.decode(logoutRequest.getAccessToken());

        // 기존에 있는 토큰으로는 재발급 못받도록 , 리프래시 토큰 삭제
        redisService.deleteValues(logoutRequest.getAccessToken() + request.getRemoteAddr());
        // 유저아이디 담은 레디스 삭제
        redisService.deleteValues(jwt.getSubject() + request.getRemoteAddr());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
