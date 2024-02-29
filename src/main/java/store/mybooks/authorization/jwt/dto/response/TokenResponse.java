package store.mybooks.authorization.jwt.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : store.mybooks.authorization.auth.service.dto.response<br>
 * fileName       : TokenResponse<br>
 * author         : masiljangajji<br>
 * date           : 2/28/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2/28/24        masiljangajji       최초 생성
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {

    private String accessToken;

    private String refreshToken;

}
