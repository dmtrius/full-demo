package com.example.demo.apps.tasks.clone;

public class Student implements Cloneable {
    String name;
    Address address;
    public Student(String name, Address address) {
        this.name = name;
        this.address = address;
    }
    @Override
    protected Student clone() throws CloneNotSupportedException {
        Student clonedStudent = (Student) super.clone();
        clonedStudent.address = address.clone(); // Clone nested Address object
        return clonedStudent;
    }
}
