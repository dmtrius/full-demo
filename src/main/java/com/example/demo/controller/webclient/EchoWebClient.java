package com.example.demo.controller.webclient;

import org.apache.tools.ant.taskdefs.Echo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequestMapping("/webclient")
@RestController
public class EchoWebClient {
    private final WebClient webClient;

    public EchoWebClient(WebClient webClient) {
        this.webClient = webClient;
    }


    @GetMapping("/echo/{message}")
    public Mono<Echo> echo(@PathVariable String message) {
        return webClient
                .get()
                .uri("/echo/" + message)
                .retrieve()
                .bodyToMono(Echo.class);
    }

    // blocking
    @GetMapping("/echo2/{message}")
    public Echo echo2(@PathVariable String message) {
        return webClient
                .get()
                .uri("/echo/" + message)
                .retrieve()
                .bodyToMono(Echo.class)
                .block();
    }
}
