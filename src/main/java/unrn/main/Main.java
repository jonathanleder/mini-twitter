package unrn.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"unrn.*"})
@EnableMongoRepositories(basePackages = "unrn.repositorios")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}


