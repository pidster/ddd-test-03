package com.insurance.claim.claimservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ClaimServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ClaimServiceApplication.class, args);
    }
}