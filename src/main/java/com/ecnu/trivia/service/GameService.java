package com.ecnu.trivia.service;


import com.ecnu.trivia.dto.Game;
import com.ecnu.trivia.dto.Player;
import com.ecnu.trivia.model.User;

import java.util.List;

/**
 * Created by joy12 on 2017/12/9.
 */
public interface GameService {

    List<Game> getAllTables();

    List<Player> getPlayersByTable(int tableId);

    boolean userChooseTable(int tableId, User user, int initialPlace);

    void setPlayerReady(int tableId, int playerId);

    void stopDice(int tableId);

    void answerQuestion(int tableId, boolean isCorrect);

}
