package com.example.demo.apps.jni;

public class HelloWorldJNI2 {
    public native void print_Hello();

    static {
        System.loadLibrary("hello");
    }

    void main () {
        System.out.println("In this Program we will learn about Java Native");
        HelloWorldJNI2 gfg = new HelloWorldJNI2();
        gfg.print_Hello();
    }
}
