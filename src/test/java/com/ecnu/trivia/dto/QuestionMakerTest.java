package com.ecnu.trivia.dto;

import com.ecnu.trivia.model.Question;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;

/**
 * Created by joy12 on 2017/12/27.
 */
public class QuestionMakerTest {
    List<Question> questionList;
    @Before
    public void setAllPrivateVariableAccessible(){
        questionList = new ArrayList<>();
        for (int i=0; i<10; i++){
            questionList.add(new Question(i,"title","domain","answers", "rightAnswer", 0));
        }
    }

    @Test
    public void addPopQuestionListSuccess() throws Exception {
        QuestionMaker questionMaker = new QuestionMaker();
        Field field = QuestionMaker.class.getDeclaredField("popQuestions");
        field.setAccessible(true);

        Assert.assertTrue(questionList.size() < QuestionMaker.MAX_NUMBER_OF_QUESTIONS);
        Assert.assertTrue(questionMaker.addPopQuestionList(questionList));

        Assert.assertTrue(((List<Question>)field.get(questionMaker)).containsAll(questionList));
    }

    @Test
    public void addPopQuestionListFail() throws Exception {
        QuestionMaker questionMaker = new QuestionMaker();
        Field field = QuestionMaker.class.getDeclaredField("popQuestions");
        field.setAccessible(true);

        List<Question> okList = new ArrayList<>();
        List<Question> failList = new ArrayList<>();
        failList.add(new Question());

        for (int i = 0; i < QuestionMaker.MAX_NUMBER_OF_QUESTIONS; i += questionList.size()) {
            okList.addAll(questionList);
        }
        for (int i = okList.size()+1; i <= QuestionMaker.MAX_NUMBER_OF_QUESTIONS; i++) {
            okList.add(questionList.get(0));
        }

        Assert.assertTrue(questionMaker.addPopQuestionList(okList));
        Assert.assertTrue(!questionMaker.addPopQuestionList(failList));
        Assert.assertTrue(!((List<Question>)field.get(questionMaker)).containsAll(failList));
    }

    @Test
    public void addScienceQuestionListSuccess() throws Exception {
        QuestionMaker questionMaker = new QuestionMaker();
        Field field = QuestionMaker.class.getDeclaredField("scienceQuestions");
        field.setAccessible(true);

        List<Question> okList = new ArrayList<>();
        List<Question> failList = new ArrayList<>();
        failList.add(new Question());

        for (int i = 0; i < QuestionMaker.MAX_NUMBER_OF_QUESTIONS; i += questionList.size()) {
            okList.addAll(questionList);
        }
        for (int i = okList.size()+1; i <= QuestionMaker.MAX_NUMBER_OF_QUESTIONS; i++) {
            okList.add(questionList.get(0));
        }

        Assert.assertTrue(questionMaker.addScienceQuestionList(okList));
        Assert.assertTrue(!questionMaker.addScienceQuestionList(failList));
        Assert.assertTrue(!((List<Question>)field.get(questionMaker)).containsAll(failList));
    }

    @Test
    public void addSportsQuestionListSuccess() throws Exception {
        QuestionMaker questionMaker = new QuestionMaker();
        Field field = QuestionMaker.class.getDeclaredField("sportsQuestions");
        field.setAccessible(true);

        List<Question> okList = new ArrayList<>();
        List<Question> failList = new ArrayList<>();
        failList.add(new Question());

        for (int i = 0; i < QuestionMaker.MAX_NUMBER_OF_QUESTIONS; i += questionList.size()) {
            okList.addAll(questionList);
        }
        for (int i = okList.size()+1; i <= QuestionMaker.MAX_NUMBER_OF_QUESTIONS; i++) {
            okList.add(questionList.get(0));
        }

        Assert.assertTrue(questionMaker.addSportsQuestionList(okList));
        Assert.assertTrue(!questionMaker.addSportsQuestionList(failList));
        Assert.assertTrue(!((List<Question>)field.get(questionMaker)).containsAll(failList));
    }

    @Test
    public void addRockQuestionListSuccess() throws Exception {
        QuestionMaker questionMaker = new QuestionMaker();
        Field field = QuestionMaker.class.getDeclaredField("rockQuestions");
        field.setAccessible(true);

        List<Question> okList = new ArrayList<>();
        List<Question> failList = new ArrayList<>();
        failList.add(new Question());

        for (int i = 0; i < QuestionMaker.MAX_NUMBER_OF_QUESTIONS; i += questionList.size()) {
            okList.addAll(questionList);
        }
        for (int i = okList.size()+1; i <= QuestionMaker.MAX_NUMBER_OF_QUESTIONS; i++) {
            okList.add(questionList.get(0));
        }

        Assert.assertTrue(questionMaker.addRockQuestionList(okList));
        Assert.assertTrue(!questionMaker.addRockQuestionList(failList));
        Assert.assertTrue(!((List<Question>)field.get(questionMaker)).containsAll(failList));
    }

    @Test
    public void distributePopQuestionSuccess() throws Exception {
        QuestionMaker questionMaker = new QuestionMaker();
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            questions.add(new Question(i,"title","pop","answers", "rightAnswer", 0));
        }

        questionMaker.addPopQuestionList(questions);

        Question question = questionMaker.distributePopQuestion();

        Assert.assertThat(questions,hasItem(question));
    }

    @Test
    public void distributeScienceQuestion() throws Exception {
        QuestionMaker questionMaker = new QuestionMaker();
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            questions.add(new Question(i,"title","science","answers", "rightAnswer", 0));
        }

        questionMaker.addScienceQuestionList(questions);

        Question question = questionMaker.distributeScienceQuestion();

        Assert.assertThat(questions,hasItem(question));
    }

    @Test
    public void distributeSportsQuestion() throws Exception {
        QuestionMaker questionMaker = new QuestionMaker();
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            questions.add(new Question(i,"title","sports","answers", "rightAnswer", 0));
        }

        questionMaker.addSportsQuestionList(questions);

        Question question = questionMaker.distributeSportsQuestion();

        Assert.assertThat(questions,hasItem(question));
    }

    @Test
    public void distributeRockQuestion() throws Exception {
        QuestionMaker questionMaker = new QuestionMaker();
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            questions.add(new Question(i,"title","rock","answers", "rightAnswer", 0));
        }

        questionMaker.addRockQuestionList(questions);

        Question question = questionMaker.distributeRockQuestion();

        Assert.assertThat(questions,hasItem(question));
    }

}