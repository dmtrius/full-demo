package com.example.demo.controller.webclient;

import com.example.demo.controller.declaritive.EchoService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Component
public class WebClientProxy {
    @Bean("wc")
    public EchoService echoService() {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:8282").build();
        WebClientAdapter adapter = WebClientAdapter.create(webClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(EchoService.class);
    }
}
