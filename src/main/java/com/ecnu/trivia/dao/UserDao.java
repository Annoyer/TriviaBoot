package com.ecnu.trivia.dao;



import com.ecnu.trivia.model.User;
import org.apache.ibatis.annotations.Param;

/**
 * Created by joy12 on 2017/12/3.
 */
public interface UserDao {
    Integer addUser(User user);
    User getUser(@Param("username") String username, @Param("password") String password);
    User getUserById(@Param("id") int id);
    User getUserByNickName(@Param("username") String username);
    int updateWinCountAndLevel(@Param("id") int id, @Param("winCount") int winCount, @Param("level") int level);
    int updateLoseCount(@Param("id") int id, @Param("loseCount") int loseCount);
}
