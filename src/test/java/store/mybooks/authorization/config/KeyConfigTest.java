package store.mybooks.authorization.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

/**
 * packageName    : store.mybooks.authorization.config<br>
 * fileName       : KeyConfigTest<br>
 * author         : masiljangajji<br>
 * date           : 3/21/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/21/24        masiljangajji       최초 생성
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import(KeyConfig.class)
public class KeyConfigTest {

    @MockBean
    private RestTemplate restTemplate;

    @Autowired
    private KeyConfig keyConfig;

    @Test
    void testKeyStore(@Mock String responseBody) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);



        String secret = "testSecret";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(responseBody, responseHeaders, HttpStatus.OK);

        when(restTemplate.exchange(any(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
                .thenReturn(responseEntity);

        when(responseEntity.getBody()).thenReturn(responseBody);

        String keyId = "testKeyId";
        String result = keyConfig.keyStore(keyId);

        verify(restTemplate).exchange(any(), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class));

        assertEquals(secret, result);
    }
}