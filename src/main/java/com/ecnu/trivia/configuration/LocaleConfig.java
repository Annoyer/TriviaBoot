package com.ecnu.trivia.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig {
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver result = new CookieLocaleResolver();
        result.setCookieName("language");
        result.setCookieMaxAge(Integer.MAX_VALUE);
        result.setDefaultLocale(Locale.ENGLISH);
        return result;
    }
}
