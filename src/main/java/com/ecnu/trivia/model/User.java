package com.ecnu.trivia.model;

/**
 * Created by joy12 on 2017/12/3.
 */
public class User {
    private Integer id;
    private String password;
    private String username;
    private Integer level;
    private Integer winCount;
    private Integer loseCount;

    public User(Integer id, String password, String username, Integer level, Integer winCount, Integer loseCount) {
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

    public Integer getId() {
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getWinCount() {
        return winCount;
    }

    public void setWinCount(Integer winCount) {
        this.winCount = winCount;
    }

    public Integer getLoseCount() {
        return loseCount;
    }

    public void setLoseCount(Integer loseCount) {
        this.loseCount = loseCount;
    }

    public String toString(){
        return "[id="+id+",username="+username+",password="+password+",level="+
                level+",winCount="+winCount+",loseCount="+loseCount;
    }
}
