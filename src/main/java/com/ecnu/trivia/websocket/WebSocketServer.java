package com.ecnu.trivia.websocket;

import com.ecnu.trivia.dto.Game;
import com.ecnu.trivia.dto.Player;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joy12 on 2017/12/25.
 */
public class WebSocketServer implements WebSocketHandler {

    //日志记录
    private static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    private static Map<Integer, Game> tables = new HashMap<Integer, Game>();
    static {
        for (int i=0 ;i<Game.MAX_TABLE_NUM; i++){
            tables.put(i,new Game(i));
        }
    }

    //记录每个在游戏页面的用户终端的连接
    private static Map<Integer, WebSocketSession> userSessionsInGames = new HashMap<>();
    //记录每个在选桌页面的用户终端的连接
    private static Map<Integer, WebSocketSession> userSessionsInTables = new HashMap<>();

    //需要session来对用户发送数据, 获取连接特征userId
//    private Integer userId;
//    private Integer tableId;


    //初次链接成功执行
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        Integer userId = (Integer) webSocketSession.getAttributes().get("userId");

        Integer tableId = (Integer) webSocketSession.getAttributes().get("tableId");
        if (tableId < 0){
            logger.info("socket tables on open()");
            userSessionsInTables.put(userId,webSocketSession);
        } else {
            logger.info("socket games on open()");
            userSessionsInGames.put(userId, webSocketSession);
            Game game = tables.get(tableId);
            if (game.hasPlayer(userId)){
                for (Player p : game.getPlayers()) {
                    if (p.getUser().getId().equals(userId)){
                        p.setConnected(true);
                        sendMessageToUserInGame(userId,game.getGameStatus().toString());
                    }
                }
            }
        }

    }

    //接受消息处理消息
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        Integer userId = (Integer) webSocketSession.getAttributes().get("userId");
        logger.debug("用户id为：{}的连接发送错误", userId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        logger.info("socket on close()");
        Integer userId = (Integer) webSocketSession.getAttributes().get("userId");
        Integer tableId = (Integer) webSocketSession.getAttributes().get("tableId");
        if (tableId < 0) {
            userSessionsInTables.remove(userId);
            logger.info("用户{}下线", userId);
        } else {
            Game table = tables.get(tableId);

            //确保当前用户是在桌上的（正常的通过选桌页面进来的）
            if (table != null) {
                Player player = table.getPlayer(userId);
                if (player != null){
                    player.setConnected(false);
                    //有一个人掉线了，游戏就得结束
                    if (table.isGameStart()) {
                        table.endGame();
                        logger.info("桌号{}游戏强制结束", tableId);
                    } else {
                        table.remove(userId);
                        logger.info("用户{}离开桌号{}", userId, tableId);
                        if (table.getPlayers().size() == 0) {
                            //removeTable(table.getTableId());
                            table.endGame();
                        }
                    }
                }
            }

            //移除当前用户终端登录的websocket信息,如果该用户下线了，则删除该用户的记录
            if (userSessionsInGames.get(userId) != null) {
                userSessionsInGames.remove(userId);
            }
            logger.info("用户{}离开桌子", userId);

            broadcastToUserInTablesPage();

        }


    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 给某个用户发送消息
     *
     * @param userId
     * @param message
     */
    public static boolean sendMessageToUserInGame(Integer userId, String message) {
        if (userSessionsInGames.containsKey(userId)) {
            logger.debug(" 给用户id为：{}的所有终端发送消息：{}", userId, message);
            WebSocketSession WS = userSessionsInGames.get(userId);
            logger.debug("sessionId为:{}", WS.getId());
            try {
                WS.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                e.printStackTrace();
                logger.debug(" 给用户id为：{}发送消息失败", userId);
                return false;
            }
            return true;
        }
        logger.debug("发送错误：当前连接不包含id为：{}的用户", userId);
        return false;
    }

    public static boolean broadcastToUserInTablesPage(){
        JSONArray array = new JSONArray();
        for (Game t: tables.values()) {
            array.add(t.getGameStatus().toString());
        }
        for (WebSocketSession WS : userSessionsInTables.values()) {
            try {
                WS.sendMessage(new TextMessage(array.toString()));
            } catch (IOException e) {
                e.printStackTrace();
                logger.debug("发送消息失败");
                return false;
            }
        }
        return true;
    }

//    public static void addTable(Game game) {
//        tables.put(game.getTableId(), game);
//    }

    public static void removeTable(int tableId) {
        tables.put(tableId,new Game(tableId));
    }

    public static Game getTable(int tableId) {
        return tables.get(tableId);
    }

    public static Map<Integer, Game> getTables() {
        return tables;
    }

}
