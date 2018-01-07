package com.ecnu.trivia.fliter;

import com.ecnu.trivia.model.User;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by joy12 on 2017/12/25.
 */
public class HttpSessionIdHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response, WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        //解决The extension [x-webkit-deflate-frame] is not supported问题
        if (request.getHeaders().containsKey("Sec-WebSocket-Extensions")) {
            request.getHeaders().set("Sec-WebSocket-Extensions", "permessage-deflate");
        }
        //检查session的值是否存在
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            User user = (User) session.getAttribute("user");
            Integer tableId = Integer.parseInt(servletRequest.getServletRequest().getParameter("tableId"));

            if (session.getAttribute("pastTableId") != null){
                attributes.put("pastTableId", session.getAttribute("pastTableId"));
            } else {
                attributes.put("pastTableId", -2);
            }

            attributes.put("tableId", tableId);
            //把session和accountId存放起来
            attributes.put("sessionId", session.getId());
            attributes.put("userId", user.getId());

            session.setAttribute("pastTableId",tableId);
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }


    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response, WebSocketHandler wsHandler,
                               Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}



