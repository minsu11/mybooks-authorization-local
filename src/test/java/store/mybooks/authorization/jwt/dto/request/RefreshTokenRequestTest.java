package store.mybooks.authorization.jwt.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * packageName    : store.mybooks.authorization.jwt.dto.request<br>
 * fileName       : RefreshTokenRequestTest<br>
 * author         : masiljangajji<br>
 * date           : 3/21/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/21/24        masiljangajji       최초 생성
 */
public class RefreshTokenRequestTest {

    @Test
    @DisplayName("RefreshTokenRequest 테스트")
    void refreshTokenRequestTest() {
        String accessToken = "sampleAccessToken";
        String ip = "192.168.1.1";
        String userAgent = "Mozilla/5.0";

        RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(accessToken, ip, userAgent);

        assertEquals(accessToken, refreshTokenRequest.getAccessToken());
        assertEquals(ip, refreshTokenRequest.getIp());
        assertEquals(userAgent, refreshTokenRequest.getUserAgent());
    }


}