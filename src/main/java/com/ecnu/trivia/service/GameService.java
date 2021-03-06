package com.ecnu.trivia.service;


import com.ecnu.trivia.dto.Game;
import com.ecnu.trivia.dto.GameStatus;
import com.ecnu.trivia.dto.Player;
import com.ecnu.trivia.model.User;

import java.util.List;

/**
 * Created by joy12 on 2017/12/9.
 */
public interface GameService {

    List<GameStatus> getAllTables();

    List<Player> getPlayersByTable(int tableId,User user);

    boolean userChooseTable(int tableId, User user, int initialPlace);

    void setPlayerReady(int tableId, int playerId);

    void stopDice(int tableId,int num);

    void answerQuestion(int tableId, boolean isCorrect);

}
