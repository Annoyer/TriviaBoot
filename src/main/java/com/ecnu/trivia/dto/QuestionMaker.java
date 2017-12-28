package com.ecnu.trivia.dto;


import com.ecnu.trivia.model.Question;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by benwu on 14-5-28.
 * 负责分配问题
 */
public class QuestionMaker {
    public static final int MAX_NUMBER_OF_QUESTIONS = 100; //最大问题数，可更改
    // by j: 分类问题列表，游戏初始化时从数据库取出; 问题类从Question修改为数据库对应模型Question类
    //ps：Question应该有Check方法
    private LinkedList<Question> popQuestions = new LinkedList<Question>();
    private LinkedList<Question> scienceQuestions = new LinkedList<Question>();
    private LinkedList<Question> sportsQuestions = new LinkedList<Question>();
    private LinkedList<Question> rockQuestions = new LinkedList<Question>();

//    private String errorMsg;// by j：错误信息

    public boolean addPopQuestionList(List<Question> popQuestion) {
        if (popQuestions.size() + popQuestion.size() <= MAX_NUMBER_OF_QUESTIONS){
            popQuestions.addAll(popQuestion);
            return true;
        } else {
            return false;
        }

    }

    public boolean addScienceQuestionList(List<Question> scienceQuestion) {
        if (scienceQuestions.size() + scienceQuestion.size() <= MAX_NUMBER_OF_QUESTIONS){
            scienceQuestions.addAll(scienceQuestion);
            return true;
        } else {
            return false;
        }
    }

    public boolean addSportsQuestionList(List<Question> sportsQuestion) {
        if (sportsQuestions.size() + sportsQuestion.size() <= MAX_NUMBER_OF_QUESTIONS){
            sportsQuestions.addAll(sportsQuestion);
            return true;
        } else {
            return false;
        }
    }

    public boolean addRockQuestionList(List<Question> rockQuestion) {
        if (rockQuestions.size() + rockQuestion.size() <= MAX_NUMBER_OF_QUESTIONS){
            rockQuestions.addAll(rockQuestion);
            return true;
        } else {
            return false;
        }
    }

//    public void addPopQuestion(Question popQuestion) {
//        popQuestions.add(popQuestion);
//    }
//
//    public void addScienceQuestion(Question scienceQuestion) {
//        scienceQuestions.add(scienceQuestion);
//    }
//
//    public void addSportsQuestion(Question sportsQuestion) {
//        sportsQuestions.add(sportsQuestion);
//    }
//
//    public void addRockQuestion(Question rockQuestion) {
//        rockQuestions.add(rockQuestion);
//    }

    /**
     * 分配问题的方式从拿出队列的第一个问题并将其删去，改成随机访问队列中的任意一个问题
     * 并做好判空
     */
    public Question distributePopQuestion() {
//        if (popQuestions == null || popQuestions.size() == 0) {
//            errorMsg = "哦！我没有这类型的问题！";
//            return new Question(errorMsg);
//        }
        return popQuestions.get(randomNum(popQuestions.size()));
    }

    public Question distributeScienceQuestion() {
//        if (scienceQuestions == null || scienceQuestions.size() == 0) {
//            errorMsg = "哦！我没有这类型的问题！";
//            return new Question(errorMsg);
//        }
        return scienceQuestions.get(randomNum(scienceQuestions.size()));
    }

    public Question distributeSportsQuestion() {
//        if (sportsQuestions == null || sportsQuestions.size() == 0) {
//            errorMsg = "哦！我没有这类型的问题！";
//            return new Question(errorMsg);
//        }
        return sportsQuestions.get(randomNum(sportsQuestions.size()));
    }

    public Question distributeRockQuestion() {
//        if (rockQuestions == null || rockQuestions.size() == 0) {
//            errorMsg = "哦！我没有这类型的问题！";
//            return new Question(errorMsg);
//        }
        return rockQuestions.get(randomNum(rockQuestions.size()));
    }

    private int randomNum(int len) {
        Random random = new Random();
        return random.nextInt(len);
    }


}
