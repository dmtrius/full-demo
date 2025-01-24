package com.example.demo.apps;

import lombok.Data;

public class App11 {
    public static void main(String... args) {
        Address address = new Address("al1", "al2");
        PersonRecord person = new PersonRecord("John Doe", address);
        System.out.println(person);
        address.setLine2("al2-2");
        System.out.println(person);
    }
}

record PersonRecord(String name, Address address) {}

@Data
class Address {
    private String line1;
    private String line2;

    public Address(String line1, String line2) {
        this.line1 = line1;
        this.line2 = line2;
    }
}
