package com.ecnu.trivia.dto;


import com.ecnu.trivia.model.Question;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Created by joy12 on 2017/12/9.
 * 封装游戏过程中的实时状态，给websocket广播用
 */
public class GameStatus {
    private int tableId;
    private List<Player> players;
    private Question currentQuestion;
    private int currentPlayerId;
    private int currentPlayerIndex; //记录当前玩家在players中的位置
    private String msg;
    private int status; //0.开始前玩家发生变化 1.第一轮游戏 2.掷骰子以后 3.回答正确 4.回答错误 -1.游戏结束
    private int dice;
    private boolean isFirstRound = true;
    private Player winner;

    public GameStatus(Game game) {
        tableId = game.getTableId();
        players = game.getPlayers();
        currentPlayerId = game.getCurrentPlayerId();
        status = game.getStatus();
        currentPlayerIndex=game.getCurrentPlayer();
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    public void setCurrentPlayerId(int currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public int getCurrentPlayerIndex() { return currentPlayerIndex; }

    public void setCurrentPlayerIndex(int currentPlayerIndex) { this.currentPlayerIndex=currentPlayerIndex; }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getDice() {
        return dice;
    }

    public void setDice(int dice) {
        this.dice = dice;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public boolean isFirstRound() {
        return isFirstRound;
    }

    public void setFirstRound(boolean firstRound) {
        isFirstRound = firstRound;
    }

    @Override
    public String toString(){
        JSONObject o = JSONObject.fromObject(this);
        return o.toString();
    }
}
