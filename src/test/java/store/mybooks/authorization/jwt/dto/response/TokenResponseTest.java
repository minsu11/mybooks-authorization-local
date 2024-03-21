package store.mybooks.authorization.jwt.dto.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * packageName    : store.mybooks.authorization.jwt.dto.response<br>
 * fileName       : TokenResponseTest<br>
 * author         : masiljangajji<br>
 * date           : 3/21/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/21/24        masiljangajji       최초 생성
 */
public class TokenResponseTest {

    @Test
    @DisplayName("TokenResponse 테스트")
     void tokenResponseTest() {
        String accessToken = "accessToken";
        TokenResponse tokenResponse = new TokenResponse(accessToken);
        assertEquals(accessToken, tokenResponse.getAccessToken());
    }


}