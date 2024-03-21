package store.mybooks.authorization.jwt.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * packageName    : store.mybooks.authorization.jwt.dto.request<br>
 * fileName       : LogoutRequestTest<br>
 * author         : masiljangajji<br>
 * date           : 3/21/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/21/24        masiljangajji       최초 생성
 */
class LogoutRequestTest {

    @Test
    @DisplayName("LogoutRequest 테스트")
    public void logoutRequestTest() {
        String accessToken = "sampleAccessToken";
        String ip = "192.168.1.1";
        String userAgent = "Mozilla/5.0";

        LogoutRequest logoutRequest = new LogoutRequest(accessToken, ip, userAgent);

        assertEquals(accessToken, logoutRequest.getAccessToken());
        assertEquals(ip, logoutRequest.getIp());
        assertEquals(userAgent, logoutRequest.getUserAgent());
    }

}