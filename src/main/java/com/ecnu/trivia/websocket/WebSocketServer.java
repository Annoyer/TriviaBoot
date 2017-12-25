package com.ecnu.trivia.websocket;


import com.ecnu.trivia.dto.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joy12 on 2017/12/9.
 * websocket服务端
 * 负责给游戏中的每个客户端广播消息
 */
@ServerEndpoint(value = "/websocket/{tableId}/{userId}")
@Component
public class WebSocketServer {
    //日志记录
    private Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    private static Map<Integer, Game> tables = new HashMap<Integer, Game>();

    //记录每个用户终端的连接
    private static Map<Integer, WebSocketServer> userSocket = new HashMap<Integer, WebSocketServer>();

    //需要session来对用户发送数据, 获取连接特征userId
    private Session session;
    private Integer userId;
    private Integer tableId;

    public static void addTable(Game game) {
        tables.put(game.getTableId(), game);
    }

    public static void removeTable(int tableId) {
        tables.remove(tableId);
    }

    public static Game getTable(int tableId) {
        return tables.get(tableId);
    }

    public static Map<Integer, Game> getTables() {
        return tables;
    }

    /**
     * onOpen
     * websocekt连接建立时的操作。用户加桌成功后，连接建立
     *
     * @param userId  用户id
     * @param session websocket连接的session属性
     * @throws IOException
     */
    @OnOpen
    public void onOpen(@PathParam("tableId") Integer tableId, @PathParam("userId") Integer userId, Session session) throws IOException {
        this.session = session;
        this.userId = userId;
        this.tableId = tableId;
        onlineCount++;
        //根据该用户当前是否已经在别的终端登录进行添加操作
        if (userSocket.containsKey(this.userId)) {
            logger.debug("当前用户id:{}已有其他终端登录", this.userId);
            this.onClose();
        } else {
            userSocket.put(this.userId, this);
        }
        logger.debug("用户{}登录的终端", userId);
        logger.debug("当前在线用户数为：{},所有终端个数为：{}", userSocket.size(), onlineCount);
    }

    /**
     * onClose
     * 连接关闭的操作
     * 修改记录：
     * 2017.12.24 by.jcy
     *      从桌上删除用户或删桌子之前，确定这个用户是正常地通过选桌页面入桌的，不然直接把这个用户的websocket删掉就好
     */
    @OnClose
    public void onClose() {
        logger.info("socket on close()");
        //移除当前用户终端登录的websocket信息,如果该用户下线了，则删除该用户的记录
        if (userSocket.get(this.userId) != null) {
            userSocket.remove(this.userId);
            onlineCount--;
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

    /**
     * ----这个没用到----
     * onMessage
     * 收到消息后的操作
     *
     * @param message 收到的消息
     * @param session 该连接的session属性
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        logger.debug("收到来自用户id为：{}的消息：{}", this.userId, message);
        if (session == null) logger.debug("session null");
    }

    /**
     * onError
     * 连接发生错误时候的操作
     *
     * @param session 该连接的session
     * @param error   发生的错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.debug("用户id为：{}的连接发送错误", this.userId);
        error.printStackTrace();
    }

    /**
     * sendMessageToUser
     * 发送消息给对应用户
     *
     * @param userId  用户id
     * @param message 发送的消息
     * @return 发送成功返回true，反则返回false
     */
    public boolean sendMessageToUser(Integer userId, String message) {
        if (userSocket.containsKey(userId)) {
            logger.debug(" 给用户id为：{}的所有终端发送消息：{}", userId, message);
            WebSocketServer WS = userSocket.get(userId);
            logger.debug("sessionId为:{}", WS.session.getId());
            try {
                WS.session.getBasicRemote().sendText(message);
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


}
