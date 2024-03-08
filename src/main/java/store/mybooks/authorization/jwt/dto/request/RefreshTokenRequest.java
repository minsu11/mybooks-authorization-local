package store.mybooks.authorization.jwt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
public class RefreshTokenRequest {

    private String accessToken;

}
