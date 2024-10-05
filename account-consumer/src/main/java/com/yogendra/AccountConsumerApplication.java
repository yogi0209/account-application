package com.yogendra;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.yogendra.dao")
public class AccountConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountConsumerApplication.class, args);
    }

}
