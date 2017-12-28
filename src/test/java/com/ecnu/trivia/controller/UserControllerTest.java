package com.ecnu.trivia.controller;

import com.ecnu.trivia.BaseTest;
import com.ecnu.trivia.dto.Result;
import com.ecnu.trivia.model.User;
import com.ecnu.trivia.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by joy12 on 2017/12/24.
 */
//@AutoConfigureMockMvc
public class UserControllerTest extends BaseTest {

//   @Autowired
    //controller层要用MockMvc
    private MockMvc mvc;

    private MockHttpSession session;

    @Autowired
    private WebApplicationContext wac;

    @Mock
    private UserService userService;

    @Autowired
    @InjectMocks
    private UserController userController; //待测试的controller


    //初始化执行
    @Before
    public void setUp() throws Exception {
        //MockMvcBuilders使用构建MockMvc对象   （项目拦截器有效）
        //mvc = MockMvcBuilders.webAppContextSetup(wac).build();
        mvc = MockMvcBuilders.standaloneSetup(userController).build();
        session=new MockHttpSession();
    }

    //测试完后，数据库事务回滚
    @Transactional
    @Rollback
    @Test
    public void signupSuccess() throws Exception {
        //用来json转字符串
        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder= post("/user/signup")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("username","c123")
                .param("password","123");
        User returnUser=new User();
        returnUser.setUsername("c123");
        returnUser.setPassword("123");
        when(userService.signup(any(User.class))).thenReturn(returnUser);
        Result expectResult=new Result(true);
        mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(expectResult)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    //测试完后，数据库事务回滚
    @Transactional
    @Rollback
    @Test
    public void signupFail() throws Exception {
        //用来json转字符串
        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder= post("/user/signup")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("username","c")
                .param("password","123");
        when(userService.signup(any(User.class))).thenReturn(null);
        Result expectResult=new Result(false);
        expectResult.setError("用户名已存在！");
        mvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(expectResult)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void loginSuccess() throws Exception {
        //用来json转字符串
        ObjectMapper objectMapper = new ObjectMapper();

        /* success */
        //构造 post 请求
        MockHttpServletRequestBuilder request = post("/user/login")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("username","j")
                .param("password","jjj");
        //mock从service中获得的数据
        User returnUser = new User(5,"jjj","j",1,0,0);
        when(userService.login(any(User.class))).thenReturn(returnUser);
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

        MockHttpServletRequestBuilder request = post("/user/login")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("username","j")
                .param("password","hahaha");

        Result failResult = new Result(false);
        when(userService.login(any(User.class))).thenReturn(null);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(failResult)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }



    @Test
    @Transactional
    @Rollback
    public void record() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        MockHttpServletRequestBuilder request = post("/user/recordGameResult")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("isWinner","true")
                .session((MockHttpSession)getLoginSession());

        User user = (User) getLoginSession().getAttribute("user");

        Result expectedResult = new Result(true);
        when(userService.countWinLose(eq(user),anyBoolean())).thenReturn(true);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    /**
     * 获取登入信息session
     * @return
     * @throws Exception
     */
    private HttpSession getLoginSession() throws Exception{
        // mock request get login session
        MvcResult result = mvc
                .perform((post("/user/login"))
                        .param("username","c")
                        .param("password","1"))
                .andExpect(status().isOk())
                .andReturn();
        return result.getRequest().getSession();
    }

}