package com.ecnu.trivia.dto;


import com.ecnu.trivia.model.User;

/**
 * Created by benwu on 14-5-28.
 * 游戏进行时的玩家
 */
public class Player {
    // 棋盘上每格对应的题目分类，也可以不写死
    public static final int MAX_NUMBER_OF_PLACE = 12;
    public static final int CATEGORY_POP_1 = 0;
    public static final int CATEGORY_POP_2 = 4;
    public static final int CATEGORY_POP_3 = 8;
    public static final int CATEGORY_SCIENCE_1 = 1;
    public static final int CATEGORY_SCIENCE_2 = 5;
    public static final int CATEGORY_SCIENCE_3 = 9;
    public static final int CATEGORY_SPORTS_1 = 2;
    public static final int CATEGORY_SPORTS_2 = 6;
    public static final int CATEGORY_SPORTS_3 = 10;
    public static final String POP = "Pop";
    public static final String SCIENCE = "Science";
    public static final String SPORTS = "Sports";
    public static final String ROCK = "Rock";

    private String playerName;
    private int place = 0; //当前玩家的初始位置
    private int sumOfGoldCoins = 0;
    private boolean isInPenaltyBox = false;

    //添加的属性
    private boolean isReady = false;//by j: 玩家是否同意游戏开始
    private User user; //by j: Player是游戏时的玩家角色，User是对应的用户，Player生成时初始化绑定

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public int getSumOfGoldCoins() {
        return sumOfGoldCoins;
    }

    public void setSumOfGoldCoins(int sumOfGoldCoins) {
        this.sumOfGoldCoins = sumOfGoldCoins;
    }

    public void setInPenaltyBox(boolean inPenaltyBox) {
        isInPenaltyBox = inPenaltyBox;
    }

    public void setIsReady(boolean ready) {
        isReady = ready;
    }

    public static int getMaxNumberOfPlace() {
        return MAX_NUMBER_OF_PLACE;
    }

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * by j: 构造时必须绑定对应的User
     * @param playerName username
     * @param user user
     */
    public Player(String playerName,User user,int initialPlace) {
        this.playerName = playerName;
        this.user = user;
        this.place = initialPlace;
    }

    public boolean getIsReady() {
        return isReady;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return this.playerName;
    }

    //循环走的
    public void moveForwardSteps(int steps) {
        this.place += steps;
        if (this.place > MAX_NUMBER_OF_PLACE - 1) this.place -= MAX_NUMBER_OF_PLACE;
    }

    public int getPlace() {
        return this.place;
    }

    public String getCurrentCategory() {
        if (this.place == CATEGORY_POP_1) return POP;
        if (this.place == CATEGORY_POP_2) return POP;
        if (this.place == CATEGORY_POP_3) return POP;
        if (this.place == CATEGORY_SCIENCE_1) return SCIENCE;
        if (this.place == CATEGORY_SCIENCE_2) return SCIENCE;
        if (this.place == CATEGORY_SCIENCE_3) return SCIENCE;
        if (this.place == CATEGORY_SPORTS_1) return SPORTS;
        if (this.place == CATEGORY_SPORTS_2) return SPORTS;
        if (this.place == CATEGORY_SPORTS_3) return SPORTS;
        return ROCK;
    }

    public void winAGoldCoin() {
        this.sumOfGoldCoins++;
    }

    public int countGoldCoins() {
        return this.sumOfGoldCoins;
    }

    public boolean isInPenaltyBox() {
        return this.isInPenaltyBox;
    }

    public void getOutOfPenaltyBox() {
        this.isInPenaltyBox = false;
    }

    public void sentToPenaltyBox() {
        this.isInPenaltyBox = true;
    }

}
