package com.example.demo.apps.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Random;
import java.util.UUID;

import static java.lang.IO.println;
import static net.logstash.logback.argument.StructuredArguments.kv;

public class StructuredLogging {
    private static final Logger log = LoggerFactory.getLogger(StructuredLogging.class);
    private static final Random rand = new Random();

    void main() {
        MDC.put("traceId", UUID.randomUUID().toString());
        var r = new Request(rand.nextInt(9999));
        var o = processOrder(r);
        println(o);
    }

    Order processOrder(Request request) {
        String traceId = MDC.get("traceId");
        log.info("Processing order for customer: {}", request.customerId());

        try {
            // Business logic...
            Order order = saveOrder(request);
            log.info("[G] Order processed successfully. OrderId: {}", order.id());
            log.info("[kv()] Order processed successfully",
                    kv("customerId", request.customerId()),
                    kv("orderId", order.id()));
            log.atInfo()
                    .setMessage("[atInfo] Order processed successfully")
                    .addKeyValue("orderId", order.id())
                    .addKeyValue("customerId", request.customerId())
                    .log();
            return order;
        } catch (InsufficientFundsException e) {
            log.warn("Payment failed for order. TraceId: {}, Customer {}, Reason: {}",
                    traceId, request.customerId(), e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("Unexpected error while processing order. TraceId: {}", traceId, e);
            throw new OrderProcessingException("Failed to process order", e);
        } finally {
            MDC.clear();
        }
    }

    @SuppressWarnings("unused")
    Order saveOrder(Request request) {
        int r = rand.nextInt(100);
        switch (r) {
            case Integer i when i < 10 -> throw new InsufficientFundsException("Insufficient funds!");
            case Integer i when i > 90 -> throw new RuntimeException();
            default -> {
                return new Order(rand.nextInt(9999));
            }
        }
    }
}

record Request(Integer customerId) {
}

record Order(Integer id) {
}

class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
}

class OrderProcessingException extends RuntimeException {
    public OrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}