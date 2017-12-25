package com.ecnu.trivia.websocket;

import com.ecnu.trivia.dto.Game;
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
    private Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    private static Map<Integer, Game> tables = new HashMap<Integer, Game>();

    //记录每个用户终端的连接
    private static Map<Integer, WebSocketSession> userSocket = new HashMap<>();

    //需要session来对用户发送数据, 获取连接特征userId
    private Integer userId;
    private Integer tableId;


    //初次链接成功执行
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.info("socket on open()");
        this.userId = (Integer) session.getAttributes().get("userId");
        this.tableId = (Integer)session.getAttributes().get("tableId");
        userSocket.put(this.userId, session);

    }

    //接受消息处理消息
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        logger.debug("用户id为：{}的连接发送错误", this.userId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        logger.info("socket on close()");
        //移除当前用户终端登录的websocket信息,如果该用户下线了，则删除该用户的记录
        if (userSocket.get(this.userId) != null) {
            userSocket.remove(this.userId);
        }
        Game table = tables.get(this.tableId);

        //确保当前用户是在桌上的（正常的通过选桌页面进来的）
        if (table != null && table.hasPlayer(this.userId)) {
            //有一个人掉线了，游戏就得结束
            if (table.isGameStart()) {
                table.endGame();
                logger.info("桌号{}游戏强制结束", this.tableId);
            } else {
                table.remove(this.userId);
                logger.info("用户{}离开桌号{}", this.userId, this.tableId);
                if (table.getPlayers().size() == 0) {
                    removeTable(table.getTableId());
                }
            }

        }
        logger.info("用户{}下线", this.userId);
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
    public boolean sendMessageToUser(Integer userId, String message) {
        if (userSocket.containsKey(userId)) {
            logger.debug(" 给用户id为：{}的所有终端发送消息：{}", userId, message);
            WebSocketSession WS = userSocket.get(userId);
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

    public static void addTable(Game game) {
        tables.put(game.getTableId(), game);
    }

    public static void removeTable(int tableId) {
        tables.remove(tableId);
        new Game(tableId);
    }

    public static Game getTable(int tableId) {
        return tables.get(tableId);
    }

    public static Map<Integer, Game> getTables() {
        return tables;
    }

}
