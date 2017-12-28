package com.ecnu.trivia.dto;

import com.ecnu.trivia.model.Question;
import com.ecnu.trivia.websocket.WebSocketServer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy12 on 2017/12/28.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Game.class})
public class GameQuestionTest {
    @Test
    public void prepareQuestions() throws Exception {

        QuestionMaker questionMaker = PowerMockito.mock(QuestionMaker.class);
        PowerMockito.whenNew(QuestionMaker.class).withNoArguments().thenReturn(questionMaker);

        Game game = new Game(0);
        List<Question> list1 = new ArrayList<>();
        List<Question> list2 = new ArrayList<>();
        List<Question> list3 = new ArrayList<>();
        List<Question> list4 = new ArrayList<>();

        game.prepareQuestions(list1,list2,list3,list4);

        Mockito.verify(questionMaker,Mockito.times(1)).addPopQuestionList(list1);
        Mockito.verify(questionMaker,Mockito.times(1)).addScienceQuestionList(list2);
        Mockito.verify(questionMaker,Mockito.times(1)).addSportsQuestionList(list3);
        Mockito.verify(questionMaker,Mockito.times(1)).addRockQuestionList(list4);

    }
}
