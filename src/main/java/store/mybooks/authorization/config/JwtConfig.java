package store.mybooks.authorization.config;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * packageName    : store.mybooks.authorization.config<br>
 * fileName       : JwtConfig<br>
 * author         : masiljangajji<br>
 * date           : 3/4/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/4/24        masiljangajji       최초 생성
 */

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private String secret;

    private String issuer;

    private Long expiration;

}
