package com.bytecycle.fooddonor;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Byte Cycle - Food Donor Service Platform
 * Main entry point for the Spring Boot application.
 *
 * @author Byte Cycle Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@OpenAPIDefinition(
        info = @Info(
                title = "Byte Cycle - Food Donor Service API",
                version = "1.0.0",
                description = "A platform connecting food donors with recipients to reduce urban food waste. " +
                        "Donors can create and manage food donations while receivers can browse and request available food.",
                contact = @Contact(
                        name = "Byte Cycle Team",
                        email = "support@bytecycle.com"
                ),
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/licenses/MIT"
                )
        )
)
public class ByteCycleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ByteCycleApplication.class, args);
        System.out.println("""
                
                ╔══════════════════════════════════════════════╗
                ║         🍱 BYTE CYCLE BACKEND STARTED 🍱      ║
                ║    Food Donor Service Platform is Running     ║
                ║                                              ║
                ║  API Base URL : http://localhost:8080/api     ║
                ║  Swagger UI   : http://localhost:8080/api/    ║
                ║                 swagger-ui.html               ║
                ╚══════════════════════════════════════════════╝
                """);
    }
}
