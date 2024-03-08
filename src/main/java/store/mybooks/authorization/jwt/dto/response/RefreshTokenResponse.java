package store.mybooks.authorization.jwt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : store.mybooks.authorization.jwt.dto.response<br>
 * fileName       : RefreshTokenResponse<br>
 * author         : masiljangajji<br>
 * date           : 3/7/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/7/24        masiljangajji       최초 생성
 */

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenResponse {

    private Boolean isValid;
    private String accessToken;

}
