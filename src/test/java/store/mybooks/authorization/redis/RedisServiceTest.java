package store.mybooks.authorization.redis;

import static io.lettuce.core.internal.Futures.await;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * packageName    : store.mybooks.authorization.redis<br>
 * fileName       : RedisServiceTest<br>
 * author         : masiljangajji<br>
 * date           : 3/7/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/7/24        masiljangajji       최초 생성
 */

@SpringBootTest
class RedisServiceTest {

    final String KEY = "key";
    final String VALUE = "value";
    final Duration DURATION = Duration.ofMillis(30);
    @Autowired
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        redisService.setValues(KEY, VALUE, DURATION);
    }

    @AfterEach
    void rollBack() {
        redisService.deleteValues(KEY);
    }

    @Test
    @DisplayName("Redis에 데이터를 저장시 정상 조회.")
    void givenKey_whenCallGetValues_thenReturnValue() {
        String findValue = String.valueOf(redisService.getValues(KEY));
        Assertions.assertEquals(VALUE, findValue);
    }

    @Test
    @DisplayName("Redis에 저장된 데이터를 수정.")
    void givenKeyAndValue_whenCallSetValues_thenChangeValue() {

        String updateValue = "updateValue";
        redisService.setValues(KEY, updateValue, DURATION);

        String findValue = String.valueOf(redisService.getValues(KEY));

        Assertions.assertEquals(updateValue, findValue);
        Assertions.assertNotEquals(VALUE, findValue);
    }

    @Test
    @DisplayName("Redis에 저장된 데이터 만료시간 지날시 삭제.")
    void givenExpiredValue_whenCallGetValues_thenReturnNull() throws InterruptedException {
        String findValue = redisService.getValues(KEY);
        Thread.sleep(60);
        String expiredValue = redisService.getValues(KEY);
        assertNotEquals(expiredValue, findValue);
        assertNull(expiredValue);
    }


    @Test
    @DisplayName("Redis에 저장된 데이터를 삭제")
    void givenKey_whenCallDeleteValues_thenDeleteData()  {
        redisService.deleteValues(KEY);
        String findValue = redisService.getValues(KEY);

        assertNull(findValue);
    }


}