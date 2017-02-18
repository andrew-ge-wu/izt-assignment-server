package com.izettle.assignment;

import com.izettle.assignment.service.DataStorage;
import com.izettle.assignment.service.UserStorage;
import com.izettle.assignment.service.mem.MemoryDataStorage;
import com.izettle.assignment.service.mem.MemoryUserStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public DataStorage dataStorage() {
        return new MemoryDataStorage();
    }

    @Bean
    public UserStorage userStorage() {
        return new MemoryUserStorage();
    }
}
