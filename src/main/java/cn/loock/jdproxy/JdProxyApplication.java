package cn.loock.jdproxy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigurationPackage
@SpringBootApplication
public class JdProxyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JdProxyApplication.class, args);
    }
}
