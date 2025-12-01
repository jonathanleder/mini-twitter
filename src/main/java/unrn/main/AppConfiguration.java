package unrn.main;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import unrn.service.TwitterService;
import unrn.util.EmfBuilder;
import unrn.util.EmfMySQLBuilder;

@Configuration
public class AppConfiguration {

    @Bean
    @Profile("test")
    public TwitterService createTest() {
        var emf = new EmfBuilder()
                .memory()
                .withDropAndCreateDDL()
                .build();
        return new TwitterService(emf);
    }

    @Bean
    @Profile("!test")
    public TwitterService createProduction() {
        var emf = new EmfMySQLBuilder()
                .mysql(
                        "jdbc:mysql://localhost:3306/mini-twitter?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC",
                        "user",
                        "1234")
                .build();
        return new TwitterService(emf);
    }
}