package com.fregrance.app;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    MybatisAutoConfiguration.class
})
public class FregranceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FregranceApplication.class, args);
    }
}