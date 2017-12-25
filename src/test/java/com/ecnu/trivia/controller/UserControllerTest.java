package com.ecnu.trivia.controller;

import com.ecnu.trivia.BaseTest;
import com.ecnu.trivia.dto.Result;
import com.ecnu.trivia.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by joy12 on 2017/12/24.
 */
@AutoConfigureMockMvc
public class UserControllerTest extends BaseTest {

    @Autowired
    //controller层要用MockMvc
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext wac;


    //初始化执行
    @Before
    public void setUp() throws Exception {
        //MockMvcBuilders使用构建MockMvc对象   （项目拦截器有效）
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        //mvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    //测试完后，数据库事务回滚
    @Transactional
    @Rollback
    @Test
    public void signup() throws Exception {

    }

    @Test
    public void loginSuccess() throws Exception {
        //用来json转字符串
        ObjectMapper objectMapper = new ObjectMapper();

        /* success */
        //构造 post 请求
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/user/login")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("username","j")
                .param("password","jjj");
        //预期返回的result
        Result successResult = new Result(true);
        //发送请求
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(successResult)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

    }

    @Test
    public void loginFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/user/login")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("username","j")
                .param("password","hahaha");

        Result failResult = new Result(false);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(failResult)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }



    @Test
    public void record() throws Exception {

    }

}