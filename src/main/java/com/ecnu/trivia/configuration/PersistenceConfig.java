package com.ecnu.trivia.configuration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@MapperScan("com.ecnu.trivia.dao")
@EnableTransactionManagement
public class PersistenceConfig {

}