package com.example.demo.apps;

import com.github.javafaker.Faker;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import static io.vavr.API.println;

public class App29 {
    void main() {
//        switches("Admin");
        mappings();
    }

    @Data
    static class User {
        private Integer id;
        private String name;
        private Integer age;
        private Boolean status;
    }
    void mappings() {
        var users = randUsers(20);
        var result = users.stream()
                .filter(u -> u.age >= 18 && u.status)
                .collect(Collectors.toMap(User::getId, User::getName));
        println(users);
        println(result);
    }
    static List<User> randUsers(int count) {
        var users = new ArrayList<User>();
        var rand = new Random();
        for (int i = 0; i < count; i++) {
            var user = new User();
            user.setId(i + 1);
            user.setName(Faker.instance().name().username());
            user.setAge(rand.nextInt(16, 80));
            user.setStatus(rand.nextBoolean());
            users.add(user);
        }
        return users;
    }

    private static final Map<String, Runnable> roleActions = new HashMap<>();
    static {
        roleActions.put("Admin", () -> println("Admin Panel Access Granted"));
        roleActions.put("Editor", () -> println("Editor Mode Activated"));
        roleActions.put("Viewer", () -> println("Viewer Access Enabled"));
    }
    void switches(String role) {
        roleActions.getOrDefault(role, () -> println("Guest Access")).run();
    }
}
