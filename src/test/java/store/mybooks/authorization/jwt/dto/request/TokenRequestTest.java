package store.mybooks.authorization.jwt.dto.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * packageName    : store.mybooks.authorization.jwt.dto.request<br>
 * fileName       : TokenRequestTest<br>
 * author         : masiljangajji<br>
 * date           : 3/21/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/21/24        masiljangajji       최초 생성
 */
public class TokenRequestTest {

    @Test
    @DisplayName("TokenRequest 테스트")
    void tokenRequestTest() {
        Boolean isAdmin = true;
        Long userId = 123L;
        String status = "active";
        String uuid = "sampleUUID";
        String ip = "192.168.1.1";
        String userAgent = "Mozilla/5.0";

        TokenRequest tokenRequest = new TokenRequest(isAdmin, userId, status, uuid, ip, userAgent);

        assertEquals(isAdmin, tokenRequest.getIsAdmin());
        assertEquals(userId, tokenRequest.getUserId());
        assertEquals(status, tokenRequest.getStatus());
        assertEquals(uuid, tokenRequest.getUuid());
        assertEquals(ip, tokenRequest.getIp());
        assertEquals(userAgent, tokenRequest.getUserAgent());
    }

}