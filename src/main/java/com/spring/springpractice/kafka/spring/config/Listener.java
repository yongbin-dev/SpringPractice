package com.spring.springpractice.kafka.spring.config;

import org.springframework.kafka.annotation.KafkaListener;


public class Listener {

    @KafkaListener(id = "listen1", topics = "topic1")
    public void listen1(String in) {
        System.out.println(in);
    }

}