package store.mybooks.authorization.dooray.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


/**
 * packageName    : store.mybooks.authorization.dooray.config<br>
 * fileName       : DoorayHookSender<br>
 * author         : masiljangajji<br>
 * date           : 2/25/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2/25/24        masiljangajji       최초 생성
 */
@Component
@Getter
@Setter
public class DoorayHookSender extends com.nhn.dooray.client.DoorayHookSender {

    public DoorayHookSender(@Value("${dooray.auth.url}") String url) {
        super(new RestTemplate(), url);
    }
}




