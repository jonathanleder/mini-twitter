package unrn.main;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import unrn.service.TwitterService;
import unrn.util.EmfBuilder;

@Configuration
@Profile("default")
public class AppConfiguration {

    @Bean
    public TwitterService create() {
        var emf = new EmfBuilder()
                //en memoria para demo
                .memory()
                //ojo, cada vez que levanto la app se borran los datos
                .withDropAndCreateDDL()
                .build();
        return new TwitterService(emf);
    }
}