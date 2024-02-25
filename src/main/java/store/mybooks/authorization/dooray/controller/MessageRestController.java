package store.mybooks.authorization.dooray.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.mybooks.authorization.dooray.config.MessageBot;
import store.mybooks.authorization.dooray.dto.request.PhoneNumberAuthRequest;
import store.mybooks.authorization.dooray.dto.response.PhoneNumberAuthResponse;
import store.mybooks.authorization.dooray.service.MessageSenderService;


/**
 * packageName    : store.mybooks.authorization.controller.dooray
 * fileName       : DoorayMessageController
 * author         : masiljangajji
 * date           : 2/26/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2/26/24        masiljangajji       최초 생성
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class DoorayMessageRestController {

    private final MessageSenderService messageSenderService;

    private final MessageBot messageBot;

    @GetMapping("/phone")
    public ResponseEntity<PhoneNumberAuthResponse> getPhoneNumberAuthMessage() {

        String randomNumber = messageSenderService.sendMessage(messageBot);
        PhoneNumberAuthResponse authRequest = new PhoneNumberAuthResponse(randomNumber);

        return new ResponseEntity<>(authRequest, HttpStatus.OK);
    }



}
