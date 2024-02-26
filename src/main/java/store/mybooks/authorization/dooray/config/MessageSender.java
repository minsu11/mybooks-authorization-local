package store.mybooks.authorization.dooray.config;


/**
 * packageName    : store.mybooks.authorization.dooray.config<br>
 * fileName       : MessageSender<br>
 * author         : masiljangajji<br>
 * date           : 2/25/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2/25/24        masiljangajji       최초 생성
 */
public interface MessageSender {

    boolean sendMessage(MessageBot messageBot, String message);

}

