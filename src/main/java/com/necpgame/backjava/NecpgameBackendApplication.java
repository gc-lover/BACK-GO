package com.necpgame.backjava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class NecpgameBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(NecpgameBackendApplication.class, args);
    }
}

