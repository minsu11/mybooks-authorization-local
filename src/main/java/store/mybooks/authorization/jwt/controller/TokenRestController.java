package store.mybooks.authorization.jwt.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.Duration;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@Slf4j
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
            @RequestBody TokenRequest tokenRequest) {


        String ipAddress = tokenRequest.getIp();
        String userAgent = tokenRequest.getUserAgent();

        // (UUID+ip+userAgent) Key , 유저아이디 Value
        // 토큰에는 UUID 값이 기재될 것임
        // 이 값을 gateway 에서 UUID 를 키로 사용자 Id 를 찾음  , 해킹하려면 엑세스토큰 , ip , userAgent 모두 알아야 해킹 가능 함
        redisService.setValues(tokenRequest.getUuid() + ipAddress + userAgent,
                String.valueOf(tokenRequest.getUserId()),
                Duration.ofMillis(jwtConfig.getRefreshExpiration()));

        // UUID 가 들어간 토큰
        String accessToken = authService.createAccessToken(tokenRequest);

        // Key = 엑세스 토큰 + ip + UserAgent
        // Value = (키메너지가 관리하는 암호 + ip) 를 BCrypt 로 잠근 값
        // 이것에 대해서 만료시간을 줌 , 일정시간이 지나면 자동으로 삭제되게 만듦
        // 재발급시 엑세스 토큰 + ip + UserAgent 로 찾고 ,  Value 를 키메너지가 관리하는 암호 + ip 로 matches 해 검증함
        // 만약 레디스 서버가 털리더라도 키메니저가 관리하는 암호를 모르기 떄문에 Value 검증 에서 실패하게 됨
        // 따라서 리프래시토큰에 대한 변조를 막을 수 있고 , 만료시간이 지나면 자동으로 사라지기 떄문에 관리가 가능 함
        redisService.setValues(accessToken + ipAddress + userAgent,
                passwordEncoder.encode(keyConfig.keyStore(redisConfig.getRedisValue()) + ipAddress),
                Duration.ofMillis(jwtConfig.getRefreshExpiration()));

        return new ResponseEntity<>(new TokenResponse(accessToken), HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshAccessToken(
            @RequestBody RefreshTokenRequest refreshTokenRequest) {

        String ipAddress = refreshTokenRequest.getIp();
        String userAgent = refreshTokenRequest.getUserAgent();

        String key = refreshTokenRequest.getAccessToken() + ipAddress + userAgent;
        String refreshToken = redisService.getValues(key);

        // 키 = 엑세스토큰 + ip + UserAgent
        // 벨류 = 암호화한 값 + ip 를 Bcrypt 로 잠근 값
        // 리프래시 토큰이 있고 , Value 가 검증이 완료됐다면 패스 , 아니면 false 를 담은 응답 반환
        if (Objects.isNull(refreshToken) ||
                !passwordEncoder.matches(keyConfig.keyStore(redisConfig.getRedisValue()) + ipAddress,
                        refreshToken)) { // null 이면 만료된 것 , BCrypt 로 잠근 값을 매치로 확인해 , 내가 넣어준 유효한 리프래시 토큰인지 검증
            return new ResponseEntity<>(new RefreshTokenResponse(false, null), HttpStatus.NO_CONTENT);
        }

        DecodedJWT jwt = JWT.decode(refreshTokenRequest.getAccessToken());

        String newAccessToken = authService.refreshAccessToken(jwt); // 엑세스토큰 새로 만들고
        redisService.deleteValues(key); // 기존에 있던 리프래시 토큰을 삭제

        // 리프래시토큰 갱신
        redisService.setValues(newAccessToken + ipAddress + userAgent,
                passwordEncoder.encode(keyConfig.keyStore(redisConfig.getRedisValue()) + ipAddress),
                Duration.ofMillis(jwtConfig.getRefreshExpiration()));

        // 유저 아이디를 담아놓은 레디스 갱신
        redisService.expireValues(jwt.getSubject() + ipAddress + userAgent, jwtConfig.getRefreshExpiration());

        // 새로운 엑세스 토큰을 발행 함
        return new ResponseEntity<>(new RefreshTokenResponse(true, newAccessToken), HttpStatus.CREATED);
    }


    @DeleteMapping("/logout")
    public ResponseEntity<Void> deleteRefreshToken(@RequestBody LogoutRequest logoutRequest) {
        DecodedJWT jwt = JWT.decode(logoutRequest.getAccessToken());

        String ipAddress = logoutRequest.getIp();
        String userAgent = logoutRequest.getUserAgent();
        // 기존에 있는 토큰으로는 재발급 못받도록 , 리프래시 토큰 삭제
        redisService.deleteValues(logoutRequest.getAccessToken() + ipAddress + userAgent);
        // 유저아이디 담은 레디스 삭제 , 이러면 엑세스토큰을 갖고 있더라도 유저아이디를 담은 레디스가 없기 떄문에 사용이 불가능 함
        redisService.deleteValues(jwt.getSubject() + ipAddress + userAgent);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
