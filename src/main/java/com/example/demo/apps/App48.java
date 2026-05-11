package com.example.demo.apps;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

public class App48 {
    void main() {
        IO.println("App48: Java 26 features demo");
        String KEY = System.getenv("RESEND_KEY");
        Resend resend = new Resend(KEY);
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("Acme <onboarding@resend.dev>")
                .to("dmitry.gordienko@gmail.com")
                .subject("it works!")
                .html("<strong>hello world</strong>")
                .build();
        try {
            CreateEmailResponse data = resend.emails().send(params);
            IO.println(data.getId());
        } catch (ResendException e) {
            e.printStackTrace();
        }
    }
}
