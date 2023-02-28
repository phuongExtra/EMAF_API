package com.emaf.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EventManagementFPTApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventManagementFPTApplication.class, args);
    }

}
