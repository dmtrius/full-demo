package com.example.demo.controller.sse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RequestMapping("/sse/")
@RestController
public class SseController {
    @GetMapping("/stream")
    public SseEmitter stream() throws IOException {
        SseEmitter emitter = new SseEmitter();
        // Push updates async
        emitter.send("Live update!");
        return emitter;
    }
}
