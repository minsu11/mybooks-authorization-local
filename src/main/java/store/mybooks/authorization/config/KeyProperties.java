package store.mybooks.authorization.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * packageName    : store.mybooks.front.config
 * fileName       : KeyProperties
 * author         : Fiat_lux
 * date           : 3/03/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/03/24        Fiat_lux       최초 생성
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "key.manager")
public class KeyProperties {
    private String url;
    private String path;
    private String appKey;
    private String password;
}
