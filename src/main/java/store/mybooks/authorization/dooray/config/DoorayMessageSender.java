package store.mybooks.authorization.dooray.config;

import com.nhn.dooray.client.DoorayHook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import store.mybooks.authorization.dooray.exception.DoorayHookSenderException;

@Component
@Slf4j
@RequiredArgsConstructor
public class DoorayMessageSender implements MessageSender {

    private final DoorayHookSender doorayHookSender;

    @Override
    public boolean sendMessage(MessageBot messageBot, String message) {

        try {
            doorayHookSender.send(DoorayHook.builder()
                    .botName(messageBot.getName())
                    .text(message)
                    .build());
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new DoorayHookSenderException();
        }

        return true;
    }
}

