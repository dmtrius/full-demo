package com.example.demo.apps.tasks.clone;

import java.io.Serializable;

public class Address implements Cloneable {
    String city;
    String state;

    public Address(String city, String state) {
        this.city = city;
        this.state = state;
    }

    @Override
    public Address clone() throws CloneNotSupportedException {
        return (Address) super.clone();
    }
}
