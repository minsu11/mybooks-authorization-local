package store.mybooks.authorization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * packageName    : store.mybooks.resource.security.config<br>
 * fileName       : SecurityConfig<br>
 * author         : masiljangajji<br>
 * date           : 2/26/24<br>
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2/26/24        masiljangajji       최초 생성
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {


    /**
     * methodName : passwordEncoder
     * author : masiljangajji
     * description : 비밀번호를 암호화하는 Encoder 설정
     *
     * @return password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * methodName : filterChain
     * author : masiljangajji
     * description : 메서드 체인 방식으로 Security 설정
     * 여기서 설정한 것들이 SpringSecurityFilterChain 이름의 Bean 으로 등록됨
     * 시큐리티는 모든 인증/인가는 filter 기반 동작
     * Servlet Context 가 관리하는 filter 를 Bean 형태로 사용하기 위해
     * Delegating Filter Proxy 를 사용해 SpringSecurityFilterChain 이름을 가진 Bean 에게
     * 처리를 위임하는 방식으로 동작
     *
     * @param http http
     * @return security filter chain
     * @throws Exception the exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest() // api 요청에 대해서
                .permitAll() // 전부허용
                .and()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }
}
