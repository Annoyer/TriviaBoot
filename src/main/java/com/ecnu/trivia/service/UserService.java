package com.ecnu.trivia.service;


import com.ecnu.trivia.model.User;

/**
 * Created by joy12 on 2017/12/3.
 */
public interface UserService {
    User signup(User user);
    User login(User user);
    boolean countWinLose(User user, boolean isWinner);
}
