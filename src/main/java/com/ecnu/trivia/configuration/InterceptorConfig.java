package com.ecnu.trivia.configuration;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor((HandlerInterceptor) context.getBean("authenticationInterceptor"));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
