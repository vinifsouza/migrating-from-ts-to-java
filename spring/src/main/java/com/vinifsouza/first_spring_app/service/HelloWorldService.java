package com.vinifsouza.first_spring_app.service;

import org.springframework.stereotype.Service;

@Service
public class HelloWorldService {
    public String sayHello(String name) {
        return "Service: Hello World, " + name + "!";
    }
}
