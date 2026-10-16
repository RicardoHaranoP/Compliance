package org.compliance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.map.repository.config.EnableMapRepositories;

@SpringBootApplication
@EnableFeignClients
@EnableMapRepositories(basePackages = "org.compliance.infrastructure.persistence.repository")
public class Compliance {

    public static void main(String[] args) {
        SpringApplication.run(Compliance.class, args);
    }
}
