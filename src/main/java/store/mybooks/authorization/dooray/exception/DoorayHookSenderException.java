package store.mybooks.authorization.dooray.exception;

/**
 * packageName    : store.mybooks.authorization.dooray.exception
 * fileName       : PhoneNumberAuthResponse
 * author         : masiljangajji
 * date           : 2/26/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2/26/24        masiljangajji       최초 생성
 */
public class DoorayHookSenderException extends RuntimeException{

    public DoorayHookSenderException(){
        super("DoorayHookSender Send Exception");
    }

}
