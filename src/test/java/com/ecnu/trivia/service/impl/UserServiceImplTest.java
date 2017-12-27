package com.ecnu.trivia.service.impl;

import com.ecnu.trivia.BaseTest;
import com.ecnu.trivia.TriviaApplication;
import com.ecnu.trivia.dao.UserDao;
import com.ecnu.trivia.model.User;
import com.ecnu.trivia.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by joy12 on 2017/12/24.
 */

public class UserServiceImplTest extends BaseTest{
    // @Mock标注的对象会被注入到@InjectMocks标注的对象里
    @Autowired
    @InjectMocks
    UserService userService;

    @Mock
    UserDao userDao;

    @Before
    public void setUp() throws Exception {

    }

    @Test
    @Transactional
    @Rollback
    public void signupSuccess() throws Exception {
        User paramUser=new User("c123","123");
        Integer returnId=10;
        when(userDao.getUserByNickName(anyString())).thenReturn(null);
        User returnUser=new User(anyInt(),"123","c123",1,0,0);

        if(userService.signup(paramUser)!=null) {
            System.out.println(userService.signup(paramUser));
        }
        else{
            fail("signupSuccess() failed");
        }
    }

    @Test
    @Transactional
    @Rollback
    public void signupFail() throws Exception {
        User paramUser=new User("c","123");
        User returnUser=new User();
        when(userDao.getUserByNickName(anyString())).thenReturn(returnUser);
        if(userService.signup(paramUser)==null){
            System.out.println("signupFail() ok");
        }
        else{
            fail("signupFail() failed");
        }
    }

    @Test
    public void testLogin() throws Exception {
        User paramUser=new User("c","1");
        User returnUser=new User(4,"1","c",1,0,0);
        when(userDao.getUser("c","1")).thenReturn(returnUser);
        System.out.println(userService.login(paramUser).toString());
    }

    @Test
    public void countWinLoseIfIsWinner() throws Exception {
        User user=new User(4,"1","c",1,0,0);
        //winCount=1, level+level*(level-1)*0.5==1 level=2
        userService.countWinLose(user,true);

    }

    @Test
    public void countWinLoseIfIsNotWinner() throws Exception {
        User user=new User(4,"1","c",1,0,0);
        //loseCount++
        userService.countWinLose(user,false);
    }

}