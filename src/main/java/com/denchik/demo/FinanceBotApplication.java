package com.denchik.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.denchik.demo")
@EnableScheduling
public class FinanceBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinanceBotApplication.class, args);
    }
}
