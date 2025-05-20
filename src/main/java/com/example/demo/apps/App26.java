package com.example.demo.apps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class App26 {
    void main() {
        aggregates();
    }

    private static final Random random = new Random();

    @SneakyThrows
    static void aggregates() {
        var result = getUsers(10).stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            List<String> emails = list.stream()
                                    .flatMap(u -> u.email().stream())
                                    .toList();
                            List<String> phones = list.stream()
                                    .flatMap(u -> u.phoneNumber().stream())
                                    .toList();
                            List<String> bio = list.stream().map(PUser::bio).toList();
                            return Map.of(
                                    "emails", emails,
                                    "phones", phones,
                                    "bio", bio);
                        }
                ));
        ObjectMapper mapper = new ObjectMapper();
        String prettyJson = mapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(result);

        System.out.println(prettyJson);
    }

    static void flatMaps() {
        var emails = getUsers(20).stream()
                .flatMap(u -> u.email().stream())
                .toList();
        System.out.println(emails);
    }

    private static List<PUser> getUsers(int maxCount) {
        List<PUser> users = new ArrayList<>();
        final int count = random.nextInt(1, maxCount);
        for(int i = 0; i < count; i++) {
            users.add(getRandomUser());
        }
        return users;
    }

    private static PUser getRandomUser() {
        Faker faker = new Faker();
        return new PUser(
                UUID.randomUUID().toString(),
                faker.name().fullName(),
                getRandomList(faker, "email"),
                getRandomList(faker, "phone"),
                getRandomList(faker, "address"),
                faker.shakespeare().hamletQuote(),
                faker.random().hex()
        );
    }

    private static List<String> getRandomList(Faker faker, String type) {
        final int size = random.nextInt(5);
        List<String> result = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            switch (type) {
                case "email" -> result.add(faker.internet().emailAddress());
                case "phone" -> result.add(faker.phoneNumber().phoneNumber());
                case "address" -> result.add(faker.address().streetAddress());
                default -> result.add(faker.shakespeare().asYouLikeItQuote());
            }
        }
        return result;
    }
}

record PUser(
        String id,
        String name,
        List<String> email,
        List<String> phoneNumber,
        List<String> address,
        String bio,
        String token
){}
