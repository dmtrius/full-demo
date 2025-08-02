package com.example.demo.controller.restclient;


import com.example.demo.controller.declaritive.EchoService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Component
public class RestClientProxy {
    @Bean("rc")
    public EchoService echoService() {
        RestClient restClient = RestClient.builder().baseUrl("http://localhost:8484/").build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(EchoService.class);
    }
}
