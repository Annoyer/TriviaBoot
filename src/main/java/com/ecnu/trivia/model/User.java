package com.ecnu.trivia.model;

/**
 * Created by joy12 on 2017/12/3.
 */
public class User {
    private int id;
    private String password;
    private String username;
    private int level;
    private int winCount;
    private int loseCount;

    public User(int id, String password, String username, int level, int winCount, int loseCount) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.level = level;
        this.winCount = winCount;
        this.loseCount = loseCount;
    }

    public User(){
    }


    public User(String username,String password) {
        this.username=username;
        this.password=password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getLoseCount() {
        return loseCount;
    }

    public void setLoseCount(int loseCount) {
        this.loseCount = loseCount;
    }

    public String toString(){
        return "[id="+id+",username="+username+",password="+password+",level="+
                level+",winCount="+winCount+",loseCount="+loseCount;
    }
}
