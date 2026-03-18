package com.example.demo.apps;

import lombok.SneakyThrows;

import java.net.URI;
import java.net.URISyntaxException;

public class App17 {
//    @SneakyThrows
    void main(String... args) {
        String url = "https://ggg.kp/qy?e=1#fr%20rrr";
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException _) {

        }
        System.out.println(uri.getRawFragment());
        System.out.println(uri.getFragment());
    }
}
