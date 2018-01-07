package com.ecnu.trivia.service.impl;

import com.ecnu.trivia.dao.QuestionDao;
import com.ecnu.trivia.dto.Game;
import com.ecnu.trivia.dto.GameStatus;
import com.ecnu.trivia.dto.Player;
import com.ecnu.trivia.model.User;
import com.ecnu.trivia.service.GameService;
import com.ecnu.trivia.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by joy12 on 2017/12/9.
 */
@Service
public class GameServiceImpl implements GameService {

    @Autowired
    QuestionDao questionDao;


    public List<GameStatus> getAllTables() {
        List<GameStatus> games = new ArrayList<>();
        Map<Integer,Game> tables = WebSocketServer.getTables();
        // 限制最多只有6桌
        for (int i=0; i<Game.MAX_TABLE_NUM; i++) {
            games.add(tables.get(i).getGameStatus());
        }
        return games;
    }

    /**修改记录：
     * 2017.12.24
     * bug：当gamepage上只有一个用户，而用户此时刷新页面。
     *      此时table上没有人了，被websocket从列表上移出去，但没有访问choosetable的页面，所以空桌没有被加进来。
     *      服务器会报500。
     * 解决：在remove table以后，马上再加一个空桌到列表内。同时，如果直接进入gamePage，但用户其实不在桌上，重定向回选桌页面
     * */
    public List<Player> getPlayersByTable(int tableId, User user) {
        List<Player> playerList = new ArrayList<Player>();

        Game game = WebSocketServer.getTable(tableId);

        if (!game.isFullPlayer() && !game.isGameStart()){
            game.add(user.getUsername(),user,0);
            Player player = game.getPlayer(user.getId());
            player.setConnected(true);

            playerList.addAll(game.getPlayers());
        } else {
            playerList.add(new Player(null,null,-1));
        }

        return playerList;
    }

    /**
     * 用户选桌
     * @param tableId
     * @param user
     * @return 是否加桌成功
     */
    public boolean userChooseTable(int tableId, User user, int initialPlace) {
        Game table = WebSocketServer.getTable(tableId);
//        //如果是空桌，先把桌子加进map
//        if (table == null){
//            table = new Game(tableId);
//            WebSocketServer.addTable(table);
//        }

        //若游戏不在进行中，且未满员，加桌成功
        if (!table.isFullPlayer() && !table.isGameStart()){
//            table.add(user.getUsername(),user,initialPlace);//table会生成player
            return true;
        }
        //游戏满员或正在进行，加桌失败
        return false;

    }

    /**
     * 玩家准备，同时检查，如果该桌所有人都ready且满员，则游戏开始
     * 2018.01.05 游戏开始条件更改为 桌上有两人及以上且所有人ready
     * @param tableId
     * @param userId
     */
    public void setPlayerReady(int tableId, int userId) {
        Game table = WebSocketServer.getTable(tableId);
        table.setReady(userId);
        if (table.isEnoughPlayer() && table.isAllPlayerReady()){
        //if (table.getPlayers().size() > 1 && table.isAllPlayerReady()){
            table.prepareQuestions(questionDao.selectByDomain("pop"),
                    questionDao.selectByDomain("science"),
                    questionDao.selectByDomain("sports"),
                    questionDao.selectByDomain("rock"));
            table.startGame();
        }
    }

    public void stopDice(int tableId,int num) {
        WebSocketServer.getTable(tableId).roll(num);
    }

    public void answerQuestion(int tableId, boolean isCorrect) {
        Game table = WebSocketServer.getTable(tableId);
        if (isCorrect){
            table.answeredCorrectly();
        } else {
            table.answeredWrong();
        }
    }



}
