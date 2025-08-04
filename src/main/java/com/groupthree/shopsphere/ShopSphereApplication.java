package com.groupthree.shopsphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class ShopSphereApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShopSphereApplication.class, args);
    }

    @RequestMapping("/")
    public static String index() {
        return "index.html";
    }

}
