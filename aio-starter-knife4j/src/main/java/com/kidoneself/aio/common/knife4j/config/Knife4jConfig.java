package com.kidoneself.aio.common.knife4j.config;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import javax.annotation.PostConstruct;
import java.util.Collections;

/**
 * knife接口文档配置
 *
 * @author YiiDii Wang
 * @date 2021/1/17 16:22:59
 */
@Slf4j
@Import({Knife4jProperties.class, BeanValidatorPluginsConfiguration.class})
@RequiredArgsConstructor
public class Knife4jConfig {

    private final Knife4jProperties knife4jProperties;

    @PostConstruct
    public void init() {
        log.info("===== Knife4jConfig init...");
    }

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build()
                .securityContexts(Collections.singletonList(securityContext()))
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {
        Knife4jProperties.Contact contact = knife4jProperties.getContact();
        return new ApiInfoBuilder()
                .title(knife4jProperties.getTitle())
                .contact(new Contact(contact.getName(),
                        contact.getUrl(),
                        contact.getUrl()))
                .version(knife4jProperties.getVersion())
                .build();
    }


    private AuthorizationScope[] scopes() {
        AuthorizationScope[] scopes = {};
        return scopes;
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(Collections.singletonList(new SecurityReference("OAuth2", scopes())))
                .forPaths(PathSelectors.any())
                .build();
    }

}
