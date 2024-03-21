package store.mybooks.authorization.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * packageName    : store.mybooks.authorization.config<br>
 * fileName       : RedisConfigTest<br>
 * author         : masiljangajji<br>
 * date           : 3/21/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/21/24        masiljangajji       최초 생성
 */

@SpringBootTest
@TestPropertySource(properties = {
        "redis.host=myRedisHost",
        "redis.port=myRedisPort",
        "redis.password=myRedisPassword",
        "redis.database=myRedisDatabase",
        "redis.key=myRedisKey",
        "redis.value=myRedisValue"
})
class RedisConfigTest {

    @Autowired
    RedisConfig redisConfig;


    @Test
    @DisplayName("redis config 테스트 ")
    void redisConfigTest() {
        assertNotNull(redisConfig);
        assertEquals("myRedisHost", redisConfig.getRedisHost());
        assertEquals("myRedisPort", redisConfig.getRedisPort());
        assertEquals("myRedisPassword", redisConfig.getRedisPassword());
        assertEquals("myRedisDatabase", redisConfig.getRedisDatabase());
        assertEquals("myRedisKey", redisConfig.getRedisKey());
        assertEquals("myRedisValue", redisConfig.getRedisValue());

    }
}