package com.heromakers.app.minutes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        Server http = new Server("local","http://localhost:8087","", Collections.emptyList() , new ArrayList<>());
//        Server https = new Server("test","https://minutes.startlump.com","", Collections.emptyList() , new ArrayList<VendorExtension>());


        return new Docket(DocumentationType.OAS_30)
                .servers(http) // , https)
                .useDefaultResponseMessages(false)

                .securityContexts(Arrays.asList(securityContext()))
                .securitySchemes(Arrays.asList(apiKey()))

                .select()
                .apis(RequestHandlerSelectors.basePackage("com.heromakers.app.minutes"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("GW with minutes Swagger")
                .description("GW with minutes Swagger")
                .version("1.0")
                .build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = {authorizationScope}; // new AuthorizationScope[1];
//        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference("X-AUTH-TOKEN", authorizationScopes));
    }

    private ApiKey apiKey() {
        return new ApiKey("X-AUTH-TOKEN", "X-AUTH-TOKEN", "header");
    }
}