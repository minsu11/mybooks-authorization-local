package store.mybooks.authorization.jwt.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : store.mybooks.authorization.jwt.dto.request<br>
 * fileName       : LogoutRequest<br>
 * author         : masiljangajji<br>
 * date           : 3/8/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/8/24        masiljangajji       최초 생성
 */

@Getter
@NoArgsConstructor
public class LogoutRequest {

    private String accessToken;

    private String ip;

    private String userAgent;

}
