package com.pw.lovedairy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 张耀斌
 */
@Configuration
//激活swagger配置
@EnableSwagger2
public class SwaggerConfig {
    //创建RestApi文档生成
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
//指定扫描包的路径（必须）
                .apis(RequestHandlerSelectors.basePackage("com.pw.lovedairy"))
                .paths(PathSelectors.any())
                .build();
    }
    //api相关的信息说明
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("在线学习RESTful API")
                .description("rest api")
                .version("1.0")
                .build();
    }
}


