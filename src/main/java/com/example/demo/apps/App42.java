package com.example.demo.apps;

import com.github.javafaker.Faker;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.lang.IO.println;

public class App42 {
    void main() {
        m1();
        m2(2);
        m2(5);
    }

    private static final String UNKNOWN = "##Unknown##";

    void m2(int count) {
        var users = getRandUsers(count);
        var names = users.stream()
            .map(u -> Optional.ofNullable(u)
                .map(User::dep)
                .map(Dep::name)
                .orElse(UNKNOWN))
            .toList();
        println(names);
    }

    void m1() {
        var u = getRandUser();
        var o = Optional.ofNullable(u)
            .map(User::dep)
            .map(Dep::name)
            .orElse(UNKNOWN);
        println(o);
    }

    private static final Random random = new Random();

    User getRandUser() {
        float rand = random.nextFloat();
        if (rand < 0.3f) {
            return null;
        } else {
            Faker faker = new Faker();
            return new User(
                faker.name().fullName(),
                new Dep(
                    faker.internet().uuid(),
                    faker.commerce().department()
                )
            );
        }
    }

    List<User> getRandUsers(int count) {
        return random.ints(count)
            .mapToObj(_ -> getRandUser())
            .toList();
    }
}

record User (String name, Dep dep){}
record Dep ( String id, String name){}
