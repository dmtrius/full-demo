package com.example.demo.apps.tasks.clone;

public class DeepCopyExample {
    void main() throws CloneNotSupportedException {
        // Original object
        Address address = new Address("New York", "NY");
        Student originalStudent = new Student("John", address);
        // Perform deep copy
        Student clonedStudent = originalStudent.clone();
        // Modify a cloned object
        clonedStudent.name = "Alice";
        clonedStudent.address.city = "Los Angeles";
        // Print both objects
        System.out.println("Original Student: " + originalStudent.name + ", Address: "
                + originalStudent.address.city + ", " + originalStudent.address.state);
        System.out.println("Cloned Student: " + clonedStudent.name + ", Address: "
                + clonedStudent.address.city + ", " + clonedStudent.address.state);
    }
}
