package com.ecnu.trivia.websocket;

import com.ecnu.trivia.TriviaApplication;
import com.ecnu.trivia.dto.Game;
import com.ecnu.trivia.dto.Player;
import com.ecnu.trivia.model.User;
import com.ecnu.trivia.service.GameService;
import com.ecnu.trivia.service.impl.GameServiceImpl;
import net.sf.json.JSONArray;
import org.apache.commons.collections.map.HashedMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by joy12 on 2017/12/27.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TriviaApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketServerTest{

    private User user;

    @Autowired
    GameService gameService;

    @LocalServerPort
    private int port;

    @Before
    public void setUp() throws Exception {
        user = new User(5,"jjj","j",1,0,0);
    }

    @Test
    public void afterConnectionEstablishedInTablePageTest() throws Exception {
        WebSocketSession webSocketSession = Mockito.mock(WebSocketSession.class);
        Map<String,Object> map = new HashMap<>();
        map.put("userId",1);
        map.put("tableId",-1);
        Mockito.when(webSocketSession.getAttributes()).thenReturn(map);

        WebSocketServer server = new WebSocketServer();
        server.afterConnectionEstablished(webSocketSession);

        Field field = server.getClass().getDeclaredField("userSessionsInTables");
        field.setAccessible(true);

        Map<Integer, WebSocketSession> userSessionsInTables = (HashMap<Integer, WebSocketSession>)field.get(server);
        Assert.assertEquals(webSocketSession,userSessionsInTables.get(1));

    }

    @Test
    public void afterConnectionEstablishedInGamePageTest() throws Exception {
        WebSocketSession webSocketSession = Mockito.mock(WebSocketSession.class);
        Map<String,Object> map = new HashMap<>();
        gameService.userChooseTable(1,user,0);
        map.put("userId",user.getId());
        map.put("tableId",1);
        Mockito.when(webSocketSession.getAttributes()).thenReturn(map);

        WebSocketServer server = new WebSocketServer();
        server.afterConnectionEstablished(webSocketSession);

        Field field = server.getClass().getDeclaredField("userSessionsInGames");
        field.setAccessible(true);

        Map<Integer, WebSocketSession> userSessionsInGames = (HashMap<Integer, WebSocketSession>)field.get(server);
        Assert.assertEquals(userSessionsInGames.get(user.getId()),webSocketSession);

    }

    @Test
    public void afterConnectionClosedInTablePageTest() throws Exception {
        WebSocketSession webSocketSession = Mockito.mock(WebSocketSession.class);
        Map<String,Object> map = new HashMap<>();
        map.put("userId",user.getId());
        map.put("tableId",-1);
        Mockito.when(webSocketSession.getAttributes()).thenReturn(map);
        CloseStatus closeStatus = new CloseStatus(1000);

        WebSocketServer server = new WebSocketServer();
        server.afterConnectionEstablished(webSocketSession);
        server.afterConnectionClosed(webSocketSession,closeStatus);

        Field field = server.getClass().getDeclaredField("userSessionsInTables");
        field.setAccessible(true);

        Map<Integer, WebSocketSession> userSessionsInTables = (HashMap<Integer, WebSocketSession>)field.get(server);
        Assert.assertNull(userSessionsInTables.get(user.getId()));

    }

    @Test
    public void afterConnectionClosedInGamePageTest() throws Exception {
        WebSocketSession webSocketSession = Mockito.mock(WebSocketSession.class);
        Map<String,Object> map = new HashMap<>();
        map.put("userId",user.getId());
        map.put("tableId",1);
        Mockito.when(webSocketSession.getAttributes()).thenReturn(map);
        gameService.userChooseTable(1,user,0);
        CloseStatus closeStatus = new CloseStatus(1000);

        WebSocketServer server = new WebSocketServer();
        server.afterConnectionEstablished(webSocketSession);
        server.afterConnectionClosed(webSocketSession,closeStatus);

        Field field = server.getClass().getDeclaredField("userSessionsInGames");
        field.setAccessible(true);

        Map<Integer, WebSocketSession> userSessionsInGames = (HashMap<Integer, WebSocketSession>)field.get(server);
        Assert.assertNull(userSessionsInGames.get(user.getId()));

    }

    @Test
    public void afterConnectionClosedInGamePageWhenGameStartTest() throws Exception {
        WebSocketSession webSocketSession = Mockito.mock(WebSocketSession.class);
        Map<String,Object> map = new HashMap<>();
        map.put("userId",user.getId());
        map.put("tableId",2);
        Mockito.when(webSocketSession.getAttributes()).thenReturn(map);
        gameService.userChooseTable(2,user,0);
        User user2 = new User(4,"1","c",1,0,0);
        gameService.userChooseTable(2,user2,0);
        gameService.setPlayerReady(2,user.getId());
        gameService.setPlayerReady(2,user2.getId());
        CloseStatus closeStatus = new CloseStatus(1000);

        WebSocketServer server = new WebSocketServer();
        server.afterConnectionEstablished(webSocketSession);
        server.afterConnectionClosed(webSocketSession,closeStatus);

        Field field = server.getClass().getDeclaredField("userSessionsInGames");
        field.setAccessible(true);

        Map<Integer, WebSocketSession> userSessionsInGames = (HashMap<Integer, WebSocketSession>)field.get(server);
        Assert.assertNull(userSessionsInGames.get(user.getId()));

    }

    @Test
    public void sendMessageToUserInGameSuccessTest() throws Exception {
        WebSocketSession webSocketSession = Mockito.mock(WebSocketSession.class);
        Map<String,Object> map = new HashMap<>();
        map.put("userId",2);
        map.put("tableId",1);
        Mockito.when(webSocketSession.getAttributes()).thenReturn(map);

        WebSocketServer server = new WebSocketServer();
        server.afterConnectionEstablished(webSocketSession);
        boolean result = WebSocketServer.sendMessageToUserInGame(2,"test");
        Assert.assertTrue(result);
    }

    @Test
    public void sendMessageToUserInGameFailureTest() throws Exception {
        boolean result = WebSocketServer.sendMessageToUserInGame(220,"fail");
        Assert.assertTrue(!result);
    }

    @Test
    public void broadcastToUserInTablesPageTest() throws Exception {
        WebSocketSession webSocketSession = Mockito.mock(WebSocketSession.class);
        Map<String,Object> map = new HashMap<>();
        map.put("userId",1);
        map.put("tableId",-1);
        Mockito.when(webSocketSession.getAttributes()).thenReturn(map);

        WebSocketServer server = new WebSocketServer();
        server.afterConnectionEstablished(webSocketSession);

        boolean result = WebSocketServer.broadcastToUserInTablesPage();
        Assert.assertTrue(result);
    }

    @Test
    public void removeTable() throws Exception {
        WebSocketServer.removeTable(2);
        Assert.assertTrue(WebSocketServer.getTables().size() == Game.MAX_TABLE_NUM);
    }

    @Test
    public void getTable() throws Exception {
        Assert.assertTrue(WebSocketServer.getTable(1).getTableId().equals(1));
        Assert.assertTrue(WebSocketServer.getTable(Game.MAX_TABLE_NUM-1).getTableId().equals(Game.MAX_TABLE_NUM-1));
        //只有6桌，id 0~5
        Assert.assertTrue(WebSocketServer.getTable(Game.MAX_TABLE_NUM) == null);
    }

    @Test
    public void getTables() throws Exception {
        Map<Integer,Game> tables = WebSocketServer.getTables();
        Assert.assertTrue(tables.size() == Game.MAX_TABLE_NUM);
    }

}