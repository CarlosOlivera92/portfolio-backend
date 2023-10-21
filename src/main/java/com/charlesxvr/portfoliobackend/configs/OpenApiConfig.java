package com.charlesxvr.portfoliobackend.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI usesMicroserviceOpenApi() {
        return new OpenAPI()
                .info(new Info().title("SpringBoot 3 - Spring security RESTful API for Portfolio").description("SpringBoot 3 - Spring Security 6 Portfolio RESTful API").version("1.0"));
    }
}
