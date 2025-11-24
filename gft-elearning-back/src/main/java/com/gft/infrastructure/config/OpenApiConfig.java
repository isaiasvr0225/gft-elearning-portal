package com.gft.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * @package : co.com.carry.microservice.auth.infrastructure.config
 * @name : OpenApiConfig.java
 * @date : 2024-11
 * @author : Isaias Villarreal
 * @version : 1.0.0
 */

@OpenAPIDefinition(
        info = @Info(
                title = "APIs Carry Express",
                description = "APIs para el manejo de autenticación y registro de clientes, usuarios y carriers de Carry Express",
                termsOfService = "Licencia de uso de software estándar para Carry Express",
                version = "1.0.0",
                contact = @Contact(
                        name = "Isaias Villarreal",
                        url = "wa.me/573116112594",
                        email = "isaiasvillarreal0225@mail.com"
                ),
                license = @License(
                        name = "Standard Software Use License for Carry Express",
                        url = "Url here"
                )
        ),
        servers = {
                @Server(
                        description = "DEV SERVER",
                        url = "http://localhost:8081"
                ),
                @Server(
                        description = "PROD SERVER",
                        url = "Url here"
                )
        }
        /*security = @SecurityRequirement(
                name = "Security Token"
        ) */
)
public @Configuration class OpenApiConfig {}
