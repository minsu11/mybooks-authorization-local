package store.mybooks.authorization.jwt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : store.mybooks.front.jwt.dto.request<br>
 * fileName       : TokenRequest<br>
 * author         : masiljangajji<br>
 * date           : 2/28/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2/28/24        masiljangajji       최초 생성
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest {

    private Boolean isAdmin;

    private Long userId;

    private String status;

    private String uuid;

    private String ip;

    private String userAgent;


}
