package unrn.web;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import unrn.service.TwitterService;
import unrn.util.EmfBuilder;

@Configuration
@Profile("test-integracion")
public class AppTestConfiguration {
    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return new EmfBuilder()
                .memory()
                .withDropAndCreateDDL()
                .build();
    }

    @Bean
    public TwitterService agenda(EntityManagerFactory emf) {
        return new TwitterService(emf);
    }
}
