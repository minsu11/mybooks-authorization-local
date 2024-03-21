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


    /**
     * methodName : createToken
     * author : masiljangajji
     * description : 로그인시 JWT 엑세스 토큰과 Refresh Token 을 발행함
     * TokenRequest 에 유저의 정보 뿐 아니라 ip , X-User-Agent 의 부가정보도 포함
     * 토큰에 유저 아이디를 기입하지 않기 위해서 페이로드에 UUID 를 넣고 , 그에 매칭되는 userId 정보를 레디스에 삽입함
     * 이 값은 gateway 에서 UUID + ip + X-User-Agent 를 이용해 userId 를 가져올 것임
     * 리프래시 토큰은 JWT 의 형태는 아니고 , 키메니저에서 관리하는 암호값 + ip 를 비크립트로 감싼 값
     * 만약 레디스가 탈취당한다 해도 비크립트로 감싸져있기 떄문에 원문을 알 수 없고 , 키메니저가 관리하는 암호값을 모르기 떄문에 조작이 불가능함
     * 리프래시토큰은 Access Token + ip + X-User-Agent 정보를 이용해 가져올 수 있음
     *
     * @param tokenRequest request
     * @return response entity
     */
    @PostMapping
    public ResponseEntity<TokenResponse> createToken(
            @RequestBody TokenRequest tokenRequest) {


        String ipAddress = tokenRequest.getIp();
        String userAgent = tokenRequest.getUserAgent();

        redisService.setValues(tokenRequest.getUuid() + ipAddress + userAgent,
                String.valueOf(tokenRequest.getUserId()),
                Duration.ofMillis(jwtConfig.getRefreshExpiration()));

        String accessToken = authService.createAccessToken(tokenRequest);

        redisService.setValues(accessToken + ipAddress + userAgent,
                passwordEncoder.encode(keyConfig.keyStore(redisConfig.getRedisValue()) + ipAddress),
                Duration.ofMillis(jwtConfig.getRefreshExpiration()));

        return new ResponseEntity<>(new TokenResponse(accessToken), HttpStatus.CREATED);
    }

    /**
     * methodName : refreshAccessToken
     * author : masiljangajji
     * description : 엑세스토큰 만료시 재발급을 요청을 보냄
     * 엑세스토큰 + ip + X-User-Agent 를 이용해 레디스에 리프래시토큰이 존재하는지 확인
     * 만약 존재한다면 키 메니저가 관리하는 암호값 + ip 주소를 비크립트로 매치시킴 (이것으로 리프래시토큰 변조를 검증)
     * 만약 리프래시토큰이 만료되지 않고 유효하다면 새로운 엑세스 토큰을 발행하고 기존의 리프래시토큰을 레디스에서 삭제 후 새롭게 넣어줌
     * 유저 아이디를 저장하는 레디스는 만료시간을 갱신시켜줌
     *
     * @param refreshTokenRequest token request
     * @return response entity
     */
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(
            @RequestBody RefreshTokenRequest refreshTokenRequest) {


        String ipAddress = refreshTokenRequest.getIp();
        String userAgent = refreshTokenRequest.getUserAgent();

        String key = refreshTokenRequest.getAccessToken() + ipAddress + userAgent;
        String refreshToken = redisService.getValues(key);

        if (Objects.isNull(refreshToken) ||
                !passwordEncoder.matches(keyConfig.keyStore(redisConfig.getRedisValue()) + ipAddress,
                        refreshToken)) {
            return new ResponseEntity<>(new TokenResponse(null), HttpStatus.CREATED);
        }

        DecodedJWT jwt = JWT.decode(refreshTokenRequest.getAccessToken());

        String newAccessToken = authService.refreshAccessToken(jwt);
        redisService.deleteValues(key);

        redisService.setValues(newAccessToken + ipAddress + userAgent,
                passwordEncoder.encode(keyConfig.keyStore(redisConfig.getRedisValue()) + ipAddress),
                Duration.ofMillis(jwtConfig.getRefreshExpiration()));

        redisService.expireValues(jwt.getSubject() + ipAddress + userAgent, jwtConfig.getRefreshExpiration());

        return new ResponseEntity<>(new TokenResponse(newAccessToken), HttpStatus.CREATED);
    }


    /**
     * methodName : deleteRefreshToken
     * author : masiljangajji
     * description : 로그아웃 요청이 올 시 레디스에서 유저아이디와 리프래시 토큰의 정보를 삭제함
     * 기존의 엑세스 토큰은 유효하지만 로그아웃시 유저아이디를 담고있는 정보가 사라지기 떄문에
     * gateway 에서 검증시 UUID 에 해당하는 유저아이디 정보를 가져오지 못해 InValid 하다는 판단을 하게 됨
     * 따라서 로그아웃시 기존의 엑세스토큰은 무력화되는 효과를 갖게 됨
     *
     * @param logoutRequest request
     * @return response entity
     */
    @DeleteMapping("/logout")
    public ResponseEntity<Void> deleteRefreshToken(@RequestBody LogoutRequest logoutRequest) {
        DecodedJWT jwt = JWT.decode(logoutRequest.getAccessToken());

        String ipAddress = logoutRequest.getIp();
        String userAgent = logoutRequest.getUserAgent();
        redisService.deleteValues(logoutRequest.getAccessToken() + ipAddress + userAgent);
        redisService.deleteValues(jwt.getSubject() + ipAddress + userAgent);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
