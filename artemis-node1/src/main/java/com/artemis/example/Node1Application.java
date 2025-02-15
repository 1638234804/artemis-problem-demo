package com.artemis.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@SpringBootApplication
public class Node1Application {

    public static void main(String[] args) {
        SpringApplication.run(Node1Application.class);
    }

}
