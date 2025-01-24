package com.example.demo.apps;

import lombok.Setter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

// section Abstract Handler
@Setter
abstract class Handler {
    protected Handler next;

    public abstract void handleRequest(int request);
}

// section Concrete Handlers
class ConcreteHandler1 extends Handler {
    @Override
    public void handleRequest(int request) {
        if (request >= 0 && request < 10) {
            HandlerHelper.print(request, this.getClass().getName());
        } else if (next != null) {
            next.handleRequest(request);
        }
    }
}

class ConcreteHandler2 extends Handler {
    @Override
    public void handleRequest(int request) {
        if (request >= 10 && request < 20) {
            HandlerHelper.print(request, this.getClass().getName());
        } else if (next != null) {
            next.handleRequest(request);
        }
    }
}

class ConcreteHandler3 extends Handler {
    @Override
    public void handleRequest(int request) {
        if (request >= 20 && request < 30) {
            HandlerHelper.print(request, this.getClass().getCanonicalName());
        } else if (next != null) {
            next.handleRequest(request);
        }
    }
}

// section Helper
@UtilityClass
@Slf4j
class HandlerHelper {
    public static final String MSG = "Request %d handled in [Handler] %s";
    public static String format(int req, String name) {
        return String.format(MSG, req, name);
    }
    public static void print(int req, String name) {
        log.info(format(req, name));
    }
}

// section Client

public class ChainOfResponsibilityDemo {
    public static void main(String[] args) {
        // section Setup Chain of Responsibility
        Handler h1 = new ConcreteHandler1();
        Handler h2 = new ConcreteHandler2();
        Handler h3 = new ConcreteHandler3();

        h1.setNext(h2);
        h2.setNext(h3);

        // Generate and process requests
        for (int i = 0; i < 35; i += 5) {
            h1.handleRequest(i);
        }
    }
}