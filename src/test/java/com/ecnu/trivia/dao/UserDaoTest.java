package com.ecnu.trivia.dao;

import com.ecnu.trivia.BaseTest;
import com.ecnu.trivia.model.User;
import org.junit.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.*;


/**
 * Created by 86761 on 2017/12/27.
 */
public class UserDaoTest extends BaseTest {
    @Autowired
    UserDao userDao;

    @Test
    @Transactional
    @Rollback
    public void testAddUser() {
        User paramUser=new User("haha","123");
        userDao.addUser(paramUser);

        if(paramUser.getId()!=0){
            System.out.println("id="+paramUser.getId());
        }
        else{
            fail("addUserTest() failed");
        }
    }

    @Test
    public void testGetUser(){
        User user=userDao.getUser("c","1");
        assertEquals(4,user.getId());
    }

    @Test
    public void testGetUserById(){
        User user=userDao.getUserById(4);
        User expectedUser=new User(4,"1","c",1,0,0);
        assertEquals(expectedUser.toString(),user.toString());
    }

    @Test
    public void testGetUserByNickName(){
       User user=userDao.getUserByNickName("c");
       User expectedUser=new User(4,"1","c",1,0,0);
       assertEquals(expectedUser.toString(),user.toString());
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateWinCountAndLevel(){
        userDao.updateWinCountAndLevel(4,1,2);
        User expectedUser=new User(4,"1","c",2,1,0);
        assertEquals(expectedUser.toString(),userDao.getUserById(4).toString());
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateLoseCount(){
        userDao.updateLoseCount(4,1);
        User expectedUser=new User(4,"1","c",1,0,1);
        assertEquals(expectedUser.toString(),userDao.getUserById(4).toString());
    }

}
