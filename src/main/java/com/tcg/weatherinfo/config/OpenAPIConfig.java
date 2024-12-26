package com.tcg.weatherinfo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {

    @Bean
    OpenAPI weatherServiceOpenAPI() {
        Server localServer = new Server()
            .url("http://localhost:8080")
            .description("Local Development Server");
            
        return new OpenAPI()
            .info(new Info()
                .title("Weather Service API")
                .version("1.0")
                .description("API for retrieving weather information by postal code")
                .termsOfService("http://tcg.com/terms/")
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://www.apache.org/licenses/LICENSE-2.0")))
            .servers(List.of(localServer));
    }
}