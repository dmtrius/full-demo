package com.example.demo.apps.tasks.clone;

public class Address implements Cloneable {
    String city;
    String state;

    public Address(String city, String state) {
        this.city = city;
        this.state = state;
    }

    @SuppressWarnings("java:S2975")
    @Override
    public Address clone() throws CloneNotSupportedException {
        return (Address) super.clone();
    }
}
