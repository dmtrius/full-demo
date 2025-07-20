package com.example.demo.apps.lb;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.mockito.ArgumentMatchers.anyString;

public class LoadBalancerTest {

    private LoadBalancer lb;

    @BeforeEach
    public void init() {
        this.lb = new LoadBalancer();
    }

    @Test
    public void test_register() {
        String server1 = "server1";
        lb.register(server1);
        Assertions.assertEquals(lb.size(), 1);
    }

    @Test
    public void test_getServer() {
        lb.register(String.valueOf(rand.nextInt()));
        Assertions.assertEquals(anyString(), lb.getServer());
    }

    @Test
    public void test_getServersSequentially() {
        String server1 = String.valueOf(rand.nextInt());
        String server2 = String.valueOf(rand.nextInt());
        lb.register(server1);
        lb.register(server2);
        Assertions.assertEquals(server1, lb.getServer());
        Assertions.assertEquals(server2, lb.getServer());
        Assertions.assertEquals(server1, lb.getServer());
    }

    @Test
    public void test_maxsize() {
        List<String> servers = createServersList(10);
        servers.stream().forEach(i -> {
            lb.register(i);
        });
        Assertions.assertThrows(RuntimeException.class, () -> lb.register(String.valueOf(rand.nextInt())));
    }

    private Random rand = new Random();

    private List<String> createServersList(int size) {
        List<String> list = new LinkedList<>();
        for (int i = 0; i < size; ++i) {
            list.add(String.valueOf(rand.nextInt()));
        }
        return list;
    }
}
