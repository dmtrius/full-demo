package com.example.demo.apps;

import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

public class App49 {
    void main() {
        IO.println("App49: Java 26 features demo");
        m1();
        m2();
    }

    void m1() {
        WebClient wc = WebClient.builder().build();
        var result = wc.get().uri("https://jsonplaceholder.typicode.com/posts/1")
            .retrieve()
            .bodyToMono(Message.class);
        IO.println(result.block());
    }

    void m2() {
        RestClient rc = RestClient.builder().build();
        var result = rc.get().uri("https://jsonplaceholder.typicode.com/posts/2")
            .retrieve().toEntity(Message.class);
        IO.println(result.getBody());
    }

    record Message(Integer id, Integer userId, String title, String body){};
}
