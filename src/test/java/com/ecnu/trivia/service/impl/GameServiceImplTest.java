package com.ecnu.trivia.service.impl;

import com.ecnu.trivia.dao.QuestionDao;
import com.ecnu.trivia.dto.Game;
import com.ecnu.trivia.dto.GameStatus;
import com.ecnu.trivia.dto.Player;
import com.ecnu.trivia.model.User;
import com.ecnu.trivia.service.GameService;
import com.ecnu.trivia.websocket.WebSocketServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by joy12 on 2017/12/26.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WebSocketServer.class})
public class GameServiceImplTest{

    @InjectMocks
    GameService gameService = new GameServiceImpl();

    @Mock
    QuestionDao questionDao;

    User user;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(WebSocketServer.class);
        user = new User(5,"jjj","j",1,0,0);
    }


    @Test
    public void getAllTablesWhenNotFirstUserEnter() throws Exception {

        Map<Integer,Game> tables = new HashMap<>();
        for (int i=0; i<Game.MAX_TABLE_NUM; i++){
            tables.put(i,new Game(i));
        }

        PowerMockito.when(WebSocketServer.getTables()).thenReturn(tables);

        List<GameStatus> resultList = gameService.getAllTables();

        Assert.assertTrue(resultList.size()==Game.MAX_TABLE_NUM);
        for (int i=0; i<Game.MAX_TABLE_NUM; i++){
            Assert.assertTrue(resultList.get(i).getTableId() == tables.get(i).getTableId());
        }

    }


    @Test
    public void callGetPlayersByTableWhenRefreshPage() throws Exception {
        //刷新页面，或直接如数地址进入页面的话，用户不在桌上
        Game table0 = mock(Game.class);
        when(table0.hasPlayer(user.getId())).thenReturn(false);
        PowerMockito.when(WebSocketServer.getTable(0)).thenReturn(table0);

        List<Player> resultList = gameService.getPlayersByTable(0,user);

        verify(table0).hasPlayer(user.getId());
        verify(table0,never()).getPlayers();
        Assert.assertTrue(resultList.size() == 1);
        Assert.assertTrue(resultList.get(0).getPlace() == -1);

    }


    @Test
    public void callGetPlayersByTableWhenFirstEnterNormally() throws Exception {
        //通过选桌页面进入的话，用户已经被加到桌上了
        CopyOnWriteArrayList<Player> players = new CopyOnWriteArrayList<>();
        players.add(new Player(user.getUsername(),user,0));

        Game table0 = mock(Game.class);
        PowerMockito.when(WebSocketServer.getTable(0)).thenReturn(table0);
        when(table0.hasPlayer(user.getId())).thenReturn(true);
        when(table0.getPlayers()).thenReturn(players);

        List<Player> resultList = gameService.getPlayersByTable(0,user);

        verify(table0).hasPlayer(user.getId());
        verify(table0).getPlayers();
        Assert.assertTrue(resultList.size()<=4);
        for (Player p:resultList) {
            assertTrue(p.getPlace() >= 0);
        }
    }

    @Test
    public void userChooseTableSuccess() throws Exception {

        Game table0 = mock(Game.class);
        when(table0.isFullPlayer()).thenReturn(false);
        when(table0.isGameStart()).thenReturn(false);
        PowerMockito.when(WebSocketServer.getTable(0)).thenReturn(table0);

        boolean result = gameService.userChooseTable(0,user,0);

        verify(table0).add(user.getUsername(),user,0);
        Assert.assertTrue(result);
    }

    @Test
    public void userChooseTableFailBecauseGameStart() throws Exception {

        Game table0 = mock(Game.class);
        when(table0.isFullPlayer()).thenReturn(true);
        when(table0.isGameStart()).thenReturn(true);
        PowerMockito.when(WebSocketServer.getTable(0)).thenReturn(table0);

        boolean result = gameService.userChooseTable(0,user,0);

        verify(table0,never()).add(anyString(),any(User.class),anyInt());
        Assert.assertTrue(!result);
    }

    @Test
    public void userChooseTableFailBecausePlayerEnough() throws Exception {

        Game table0 = mock(Game.class);
        when(table0.isFullPlayer()).thenReturn(true);
        when(table0.isGameStart()).thenReturn(false);
        PowerMockito.when(WebSocketServer.getTable(0)).thenReturn(table0);

        boolean result = gameService.userChooseTable(0,user,0);

        verify(table0,never()).add(anyString(),any(User.class),anyInt());
        Assert.assertTrue(!result);
    }



    @Test
    public void setPlayerReadyWhenPlayerNotEnough() throws Exception {
        Game table0 = mock(Game.class);
        when(table0.isEnoughPlayer()).thenReturn(false);
        when(table0.isAllPlayerReady()).thenReturn(true);
        PowerMockito.when(WebSocketServer.getTable(0)).thenReturn(table0);

        gameService.setPlayerReady(0,user.getId());

        verify(table0).setReady(user.getId());
        verify(table0,never()).prepareQuestions(anyList(),anyList(),anyList(),anyList());
        verify(table0,never()).startGame();
    }

    @Test
    public void setPlayerReadyWhenEnoughPlayerButNotEveryoneReady() throws Exception {
        Game table0 = mock(Game.class);
        when(table0.isEnoughPlayer()).thenReturn(true);
        when(table0.isAllPlayerReady()).thenReturn(false);
        PowerMockito.when(WebSocketServer.getTable(0)).thenReturn(table0);

        gameService.setPlayerReady(0,user.getId());

        verify(table0).setReady(user.getId());
        verify(table0,never()).prepareQuestions(anyList(),anyList(),anyList(),anyList());
        verify(table0,never()).startGame();
    }

    @Test
    public void setPlayerReadyWhenEnoughPlayerAndEveryoneReadyExceptCurrentPlayer() throws Exception {
        Game table0 = mock(Game.class);
        when(table0.isEnoughPlayer()).thenReturn(true);
        when(table0.isAllPlayerReady()).thenReturn(true);
        PowerMockito.when(WebSocketServer.getTable(0)).thenReturn(table0);

        gameService.setPlayerReady(0,user.getId());

        verify(questionDao).selectByDomain("pop");
        verify(questionDao).selectByDomain("science");
        verify(questionDao).selectByDomain("sports");
        verify(questionDao).selectByDomain("rock");
        verify(table0).setReady(user.getId());
        verify(table0).prepareQuestions(anyList(),anyList(),anyList(),anyList());
        verify(table0).startGame();
    }

    @Test
    public void stopDice() throws Exception {
        Game table0 = mock(Game.class);
        PowerMockito.when(WebSocketServer.getTable(0)).thenReturn(table0);

        gameService.stopDice(0,3);

        verify(table0).roll(3);
    }

    @Test
    public void answerQuestionWhenIsCorrectIsTrue() throws Exception {
        Game table0 = mock(Game.class);
        PowerMockito.when(WebSocketServer.getTable(0)).thenReturn(table0);

        gameService.answerQuestion(0,true);

        verify(table0).answeredCorrectly();
        verify(table0,never()).answeredWrong();
    }

    @Test
    public void answerQuestionWhenIsCorrectIsFalse() throws Exception {
        Game table0 = mock(Game.class);
        PowerMockito.when(WebSocketServer.getTable(0)).thenReturn(table0);

        gameService.answerQuestion(0,false);

        verify(table0,never()).answeredCorrectly();
        verify(table0).answeredWrong();
    }

}