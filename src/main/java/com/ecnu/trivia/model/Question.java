package com.ecnu.trivia.model;

/**
 * Created by joy12 on 2017/12/9.
 */
public class Question {
    private int id;
    private String title;
    private String domain;
    private String answers;
    private String rightAnswer;
    private int level;

    public Question(int id, String title, String domain, String answers, String rightAnswer, int level) {
        this.id = id;
        this.title = title;
        this.domain = domain;
        this.answers = answers;
        this.rightAnswer = rightAnswer;
        this.level = level;
    }

//    public Question(String title) {
//        this.title = title;
//    }

    public int getId() {

        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Question() {
    }

}
