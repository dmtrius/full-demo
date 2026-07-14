package com.example.demo.apps.tasks.concurrency;

import jakarta.annotation.PostConstruct;

public class InterviewQuestionClass {

    @PostConstruct
    public void postConstruct() {
        IO.println("PostConstruct method executed");
    }

    // Instance method
    public void instanceMethod() {
        IO.println("Instance method executed");
    }

    // Static block
    static {
        IO.println("Static block executed");
    }

    // Constructor
    public InterviewQuestionClass() {
        IO.println("Constructor executed");
    }

    // Static method
    public static void staticMethod() {
        IO.println("Static method executed");
    }

    // Instance initialization block
    {
        IO.println("Instance initialization block executed");
    }

    void main() {
        InterviewQuestionClass.staticMethod();
        InterviewQuestionClass obj = new InterviewQuestionClass();
        obj.instanceMethod();
    }
}
