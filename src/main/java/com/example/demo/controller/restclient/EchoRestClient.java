package com.example.demo.controller.restclient;

import org.apache.tools.ant.taskdefs.Echo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RequestMapping("/restclient")
@RestController
public class EchoRestClient {
    private final RestClient restClient;

    public EchoRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @GetMapping("/echo/{message}")
    public Echo echo(@PathVariable String message) {
        return restClient
                .get()
                .uri("/echo/" + message)
                .retrieve()
                .body(Echo.class);
    }
}
