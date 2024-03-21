package store.mybooks.authorization.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * packageName    : store.mybooks.authorization.config<br>
 * fileName       : KeyPropertiesTest<br>
 * author         : masiljangajji<br>
 * date           : 3/21/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 3/21/24        masiljangajji       최초 생성
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "key.manager.url=testUrl",
        "key.manager.path=testPath",
        "key.manager.appKey=testAppKey",
        "key.manager.password=testPassword"
})
@EnableConfigurationProperties(KeyProperties.class)
public class KeyPropertiesTest {

    @Test
    @DisplayName("key properties 테스트")
    void testKeyProperties(@Autowired KeyProperties keyProperties) {
        String testUrl = "testUrl";
        String testPath = "testPath";
        String testAppKey = "testAppKey";
        String testPassword = "testPassword";

        assertNotNull(keyProperties);

        assertEquals(testUrl, keyProperties.getUrl());
        assertEquals(testPath, keyProperties.getPath());
        assertEquals(testAppKey, keyProperties.getAppKey());
        assertEquals(testPassword, keyProperties.getPassword());
    }

    @Configuration
    static class TestConfig {
        @Bean
        public static PropertySourcesPlaceholderConfigurer propertiesResolver(Environment environment) {
            PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
            TestPropertyValues.of("key.manager.url=testUrl",
                            "key.manager.path=testPath",
                            "key.manager.appKey=testAppKey",
                            "key.manager.password=testPassword")
                    .applyTo((ConfigurableEnvironment) environment);
            return configurer;
        }
    }
}