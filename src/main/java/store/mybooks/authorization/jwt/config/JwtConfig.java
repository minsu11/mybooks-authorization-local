package store.mybooks.authorization.jwt.config;

public interface JwtConfig {

    // todo 여기있는 것들 전부다 키체인에 넣어서 관리하기
    String SECRET = "이승재"; // 우리 서버만 알고 있는 비밀값
    String ISSUER = "my-books";
    int EXPIRATION_TIME = 1800000; // 30분 (1/1000초)

}
