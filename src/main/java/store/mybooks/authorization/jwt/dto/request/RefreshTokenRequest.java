package store.mybooks.authorization.jwt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * packageName    : store.mybooks.authorization.jwt.dto.request<br>
 * fileName       : RefreshTokenRequest<br>
 * author         : masiljangajji<br>
 * date           : 3/8/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/8/24        masiljangajji       최초 생성
 */
@Getter
@AllArgsConstructor
public class RefreshTokenRequest {

    private String accessToken;

}
