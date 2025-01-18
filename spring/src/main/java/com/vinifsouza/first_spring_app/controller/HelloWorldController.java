package com.vinifsouza.first_spring_app.controller;

import com.vinifsouza.first_spring_app.domain.User;
import com.vinifsouza.first_spring_app.service.HelloWorldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/hello-world") // endpoint
public class HelloWorldController {
    @Autowired // faz a injeção de dependência sem precisar definir no construtor
    private HelloWorldService helloWorldService;

    @GetMapping // GET /
    public HashMap<String, String> sayHello() {
        HashMap<String, String> result = new HashMap<>();
        result.put("message", helloWorldService.sayHello("Vinícius"));
        return result;
    }

    @PostMapping("/{id}")
    public HashMap<String, String> sayHelloPost(@PathVariable String id, @RequestParam String filter, @RequestBody User body) {
        HashMap<String, String> result = new HashMap<>();
        result.put("id", id);
        result.put("name", body.getName());
        result.put("email", body.getEmail());
        result.put("filter", filter);
        return result;
    }
}
