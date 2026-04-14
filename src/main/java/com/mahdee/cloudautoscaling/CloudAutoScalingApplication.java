package com.mahdee.cloudautoscaling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class CloudAutoScalingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudAutoScalingApplication.class, args);
    }

    // A simple endpoint so you can test if the app is running in Kubernetes later!
    @GetMapping("/")
    public String home() {
        return "Hello from the Cloud Auto Scaling App!";
    }
}