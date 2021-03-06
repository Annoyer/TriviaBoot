package com.ecnu.trivia.service.impl;


import com.ecnu.trivia.dao.UserDao;
import com.ecnu.trivia.model.User;
import com.ecnu.trivia.service.UserService;
import com.ecnu.trivia.websocket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by joy12 on 2017/12/3.
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserDao userDao;

    public User signup(User user){
        if(userDao.getUserByNickName(user.getUsername())!=null){
            return null;
        }
        else{
        userDao.addUser(user);
        return user;
        }
    }


    public User login(User user){
        return userDao.getUser(user.getUsername(),user.getPassword());
    }

    public boolean countWinLose(User user, boolean isWinner) {
        if (isWinner){
            int winCount = user.getWinCount()+1;
            int level = user.getLevel();
            if (winCount == level+level*(level-1)*0.5){//d=1的等差数列（1 2 3 4 5 6……）
                level++;
            }
            System.out.println("userDao.updateWinCountAndLevel("+user.getId()+","+winCount+","+level+")");
            userDao.updateWinCountAndLevel(user.getId(),winCount,level);

        } else {
            System.out.println("userDao.updateLoseCount("+user.getId()+","+(user.getLoseCount()+1)+")");
            userDao.updateLoseCount(user.getId(),user.getLoseCount()+1);

        }
        return true;
    }
}
