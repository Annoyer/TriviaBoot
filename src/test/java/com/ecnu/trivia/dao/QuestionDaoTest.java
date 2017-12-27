package com.ecnu.trivia.dao;

import com.ecnu.trivia.BaseTest;
import com.ecnu.trivia.model.Question;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by joy12 on 2017/12/27.
 */
public class QuestionDaoTest extends BaseTest {
    @Autowired
    QuestionDao questionDao;

    @Test
    public void selectByDomain() throws Exception {
        List<Question> questions = questionDao.selectByDomain("pop");

        for (Question q:questions) {
            Assert.assertEquals(q.getDomain(),"pop");
        }

        questions = questionDao.selectByDomain("science");

        for (Question q:questions) {
            Assert.assertEquals(q.getDomain(),"science");
        }

        questions = questionDao.selectByDomain("sports");

        for (Question q:questions) {
            Assert.assertEquals(q.getDomain(),"sports");
        }

        questions = questionDao.selectByDomain("rock");

        for (Question q:questions) {
            Assert.assertEquals(q.getDomain(),"rock");
        }
    }

}