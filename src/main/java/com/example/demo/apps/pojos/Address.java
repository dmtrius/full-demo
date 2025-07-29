package com.example.demo.apps.pojos;

public class Address {
    private String street;
    private String city;

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }
    public void update(String street, String city) {
        this.street = street;
        this.city = city;
    }

    public String getStreetAndCity() {
        return  street + ", " + city;
    }
}
