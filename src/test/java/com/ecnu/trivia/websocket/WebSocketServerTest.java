package com.ecnu.trivia.websocket;

import com.ecnu.trivia.TriviaApplication;
import com.ecnu.trivia.dto.Game;
import com.ecnu.trivia.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import java.util.Map;


/**
 * Created by joy12 on 2017/12/27.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TriviaApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketServerTest{

    private User user;
    private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
    private WebSocketClient tablesClient;
    private WebSocketServer server;

    @LocalServerPort
    private int port;

    @Before
    public void setUp() throws Exception {
//        tablesClient = new StandardWebSocketClient();
//        server = new WebSocketServer();
////        tablesClient.doHandshake(server,
////                "ws://localhost:"+String.valueOf(port)+"/websocket?tableId=-1");
//        WebSocketConnectionManager manager = new WebSocketConnectionManager(tablesClient,server,"ws://localhost:"+String.valueOf(port)+"/websocket?tableId=-1");
//        manager.startInternal();
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