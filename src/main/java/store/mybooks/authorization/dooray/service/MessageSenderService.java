package store.mybooks.authorization.dooray.service;

import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.mybooks.authorization.dooray.config.MessageBot;
import store.mybooks.authorization.dooray.config.MessageSender;

/**
 * packageName    : store.mybooks.authorization.service
 * fileName       : MessageSender
 * author         : masiljangajji
 * date           : 2/25/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2/25/24        masiljangajji       최초 생성
 */
@Service
@RequiredArgsConstructor
public class MessageSenderService {

    private final MessageSender messageSender;

    public String sendMessage(MessageBot messageBot) {

        Random random = new Random();

        String randomNumber = String.valueOf(random.nextInt(9000) + 1000);

        try {
            messageSender.sendMessage(messageBot, randomNumber);
        } catch (RuntimeException e) {
            return e.getMessage();
        }
        return randomNumber;
    }


}
