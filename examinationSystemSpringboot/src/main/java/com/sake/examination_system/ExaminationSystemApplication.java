package com.sake.examination_system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@SpringBootApplication
@MapperScan("com.sake.examination_system.mapper")
public class ExaminationSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExaminationSystemApplication.class, args);
    }
}
