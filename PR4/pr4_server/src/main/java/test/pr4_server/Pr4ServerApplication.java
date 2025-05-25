package test.pr4_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Pr4ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Pr4ServerApplication.class, args);
    }

}
