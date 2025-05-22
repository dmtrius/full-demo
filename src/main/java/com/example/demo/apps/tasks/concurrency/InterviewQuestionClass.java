package com.example.demo.apps.tasks.concurrency;

import jakarta.annotation.PostConstruct;

public class InterviewQuestionClass {

    // Method annotated with @PostConstruct
    @PostConstruct
    public void postConstruct() {
        System.out.println("PostConstruct method executed");
    }

    // Instance method
    public void instanceMethod() {
        System.out.println("Instance method executed");
    }

    // Static block
    static {
        System.out.println("Static block executed");
    }

    // Constructor
    public InterviewQuestionClass() {
        System.out.println("Constructor executed");
    }

    // Static method
    public static void staticMethod() {
        System.out.println("Static method executed");
    }

    // Instance initialization block
    {
        System.out.println("Instance initialization block executed");
    }


    public static void main(String[] args) {


        InterviewQuestionClass.staticMethod();

        InterviewQuestionClass obj = new InterviewQuestionClass();

        obj.instanceMethod();
    }
}
