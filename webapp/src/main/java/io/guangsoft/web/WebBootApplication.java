package io.guangsoft.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 */
@EnableSwagger2
@ComponentScan({"io.guangsoft.common.quartz.config","io.guangsoft.common.oss","io.guangsoft.common.sms","io.guangsoft.ui.tags","io.guangsoft.ui","io.guangsoft.beetl.tags","io.guangsoft.web"})
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
// @SpringBootApplication
public class WebBootApplication {
    public static void main(String[] args) throws Exception {
        SpringApplication.run(WebBootApplication.class, args);
    }
}

