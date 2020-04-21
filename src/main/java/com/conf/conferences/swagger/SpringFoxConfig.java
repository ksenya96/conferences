package com.conf.conferences.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                //.host("ec2-3-21-169-41.us-east-2.compute.amazonaws.com:8080")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.conf.conferences.controllers"))
                .paths(PathSelectors.any())
                .build()
                .tags(new Tag("oauth2-authentication", "Oauth2 authentication"))
                .apiInfo(apiInfo())
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Conferences")
                .description("Application for managing conferences info and ticketing")
                .version("1.0.0")
                .build();
    }
}
