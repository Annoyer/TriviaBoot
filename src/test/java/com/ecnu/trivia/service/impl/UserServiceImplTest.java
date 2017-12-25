package com.ecnu.trivia.service.impl;

import com.ecnu.trivia.BaseTest;
import com.ecnu.trivia.TriviaApplication;
import com.ecnu.trivia.dao.UserDao;
import com.ecnu.trivia.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;

/**
 * Created by joy12 on 2017/12/24.
 */

public class UserServiceImplTest extends BaseTest{
    // @Mock标注的对象会被注入到@InjectMocks标注的对象里
    @InjectMocks
    UserService userService;

    @Mock
    UserDao userDao;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void signup() throws Exception {

    }

    @Test
    public void login() throws Exception {

    }

    @Test
    public void countWinLose() throws Exception {

    }

}