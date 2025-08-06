package com.example.demo.apps.jni;

public class HelloWorldJNI {
    static {
        System.loadLibrary("native");
    }

    void main() {
        new HelloWorldJNI().sayHello();
    }

    // Declare a native method sayHello() that receives no arguments and returns void
    private native void sayHello();
}
