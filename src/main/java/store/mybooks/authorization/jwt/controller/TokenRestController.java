package store.mybooks.authorization.jwt.controller;

import java.util.Enumeration;
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
import store.mybooks.authorization.jwt.dto.request.TokenRequest;
import store.mybooks.authorization.jwt.dto.response.TokenResponse;
import store.mybooks.authorization.jwt.service.TokenService;

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

    @PostMapping
    public ResponseEntity<TokenResponse> createToken(
            @RequestBody TokenRequest tokenRequest) {

        String accessToken = authService.createAccessToken(tokenRequest);
        String refreshToken = authService.createRefreshToken(accessToken);

        return new ResponseEntity<>(new TokenResponse(accessToken, refreshToken), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<String> test(HttpServletRequest request) {
        String token=request.getHeader("token");
        System.out.println(token);

        return new ResponseEntity<>("good", HttpStatus.OK);
    }


}
