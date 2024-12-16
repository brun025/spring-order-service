package com.order.demo.infrastructure.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
	
	@Bean
	GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
				.group("public-apis")
				.pathsToMatch("/**")
				.build();
	}
	
	@Bean
	OpenAPI customOpenApi() {
		return new OpenAPI()
				.info(new Info().title("API Order").version("1.0"));
				// .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
				// .components(
				// 		new Components()
				// 		.addSecuritySchemes("bearerAuth", new SecurityScheme()
				// 		.type(SecurityScheme.Type.HTTP)
				// 		.scheme("bearer")
				// 		.bearerFormat("JWT")));		
	}
}
