package store.mybooks.authorization.dooray.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class MessageBot {

    @Value("${phone.auth.name}")
    private String name;


}
