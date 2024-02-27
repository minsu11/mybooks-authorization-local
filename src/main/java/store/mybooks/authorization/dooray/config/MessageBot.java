package store.mybooks.authorization.dooray.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * packageName    : store.mybooks.authorization.dooray.config<br>
 * fileName       : MessageBot<br>
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
public class MessageBot {


    @Value("${phone.auth.name}")
    private String name;


}
