package com.aces.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication

//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
public class Application {

	public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
