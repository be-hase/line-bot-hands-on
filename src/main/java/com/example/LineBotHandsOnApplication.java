package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class LineBotHandsOnApplication {

    @RequestMapping("/webhook")
    public String helloWorld() {
        return "Hello world";
    }

    public static void main(String[] args) {
        SpringApplication.run(LineBotHandsOnApplication.class, args);
    }
}
