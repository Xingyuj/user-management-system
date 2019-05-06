package com.xingyu.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xingyu.model.UserAccount;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * swagger configuration
 * @author
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket usersApi(){
        //add head start
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("Authorization").description("jwt token").modelRef(new ModelRef("string")).parameterType("header").required(true).build();
        pars.add(tokenPar.build());
        //add head end
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("usersAPI")
                .ignoredParameterTypes(UserAccount.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xingyu.controller"))
                .paths(PathSelectors.ant("/accounts/**"))
                .build()
                .globalOperationParameters(pars);
    }
    @Bean
    public Docket wap_api() {
        return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.ant("/authentications/**")).build()
                .groupName("authentication")
                .apiInfo(apiInfo());
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Ethan XingyuJi's UMS API")
                .description("A restful api demo, repo can be found here https://github.com/Xingyuj/user-management-system \n please select authentication or users api spec at upper right corner")
                .version("1.0")
                .build();
    }
}