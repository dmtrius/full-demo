package com.example.demo.controller;

import com.example.demo.service.KafkaService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kafka")
public class KafkaController {

    private final KafkaService service;

    public KafkaController(KafkaService service) {
        this.service = service;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestBody String message) {
        return service.sendMessage(message);
    }

    @PostMapping("/tsend/{topic}")
    public String sendMessage(@PathVariable String topic, @RequestBody String message) {
        return service.sendMessage(topic, message);
    }
}
