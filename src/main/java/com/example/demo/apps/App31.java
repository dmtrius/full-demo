package com.example.demo.apps;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.io.IO.println;

public class App31 {
    void main() {
        t1(1);
        toMap();
        toMap2();
    }

    void toMap() {
        List<String> names = List.of("q","w","e","r","e");
        Map<String, Integer> nameCount = names.
                stream().
                collect(
                        Collectors.toMap(Function.identity(),
                                name -> 1,
                                Integer::sum));
        println(nameCount);
    }

    void toMap2() {
        List<User> users = List.of(new User(1, "q", 20), new User(2, "w", 30), new User(3, "e", 40));
        Map<Integer, User> userMap = users.
                stream().
                collect(
                        Collectors.
                                toMap(User::id, Function.identity())
                );
        println(userMap);
    }

    record User(int id, String name, int age) {}

    void t1(Object obj) {
        if (obj instanceof String str) {
            System.out.println(str.toLowerCase());
        }

        enum Status {
            PENDING, APPROVED, REJECTED
        }

        EnumMap<Status, String> statusMessages = new EnumMap<>(Status.class);
        statusMessages.put(Status.PENDING, "Waiting for approval");
        statusMessages.put(Status.APPROVED, "Approved!");
        statusMessages.put(Status.REJECTED, "Rejected by admin");

        println(statusMessages.get(Status.PENDING));
    }
}
