package store.mybooks.authorization.dooray.config;


public interface MessageSender {

    boolean sendMessage(MessageBot messageBot, String message);

}

