package com.example.demo.apps;

import lombok.SneakyThrows;

import java.net.URI;

public class App17 {
    @SuppressWarnings("unused")
    @SneakyThrows
    public static void main(String... args) {
        String url = "https://ggg.kp/qy?e=1#fr%20rrr";
        URI uri = new URI(url);
        System.out.println(uri.getRawFragment());
        System.out.println(uri.getFragment());
    }
}
