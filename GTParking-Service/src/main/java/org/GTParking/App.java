package org.GTParking;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@MapperScan("org.GTParking.dao")
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}