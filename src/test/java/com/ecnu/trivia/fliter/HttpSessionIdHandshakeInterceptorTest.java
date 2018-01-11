package com.ecnu.trivia.fliter;

import com.ecnu.trivia.BaseTest;
import com.ecnu.trivia.model.User;
import com.ecnu.trivia.websocket.WebSocketServer;
import org.apache.catalina.session.StandardSession;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.socket.WebSocketHandler;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

/**
 * Created by joy12 on 2018/1/11.
 */
public class HttpSessionIdHandshakeInterceptorTest extends BaseTest {
    private User user;
    private ServletServerHttpRequest request;
    private ServerHttpResponse response;
    private WebSocketHandler wsHandler;
    private HttpSession session;
    private HttpHeaders headers;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        user = new User(5,"jjj","j",1,0,0);

        HttpServletRequest servletRequest = Mockito.mock(HttpServletRequest.class);
        headers = new HttpHeaders();
        request = new ServletServerHttpRequest(servletRequest);
        Field field = request.getClass().getDeclaredField("headers");
        field.setAccessible(true);
        field.set(request,headers);

        response = Mockito.mock(ServerHttpResponse.class);
        wsHandler = Mockito.mock(WebSocketHandler.class);

        session = new MockHttpSession();
        session.setAttribute("user",user);

        Mockito.when(servletRequest.getSession(false)).thenReturn(session);
        Mockito.when(servletRequest.getParameter("tableId")).thenReturn("22");
    }
    @Test
    public void beforeHandshake() throws Exception {
        Map<String,Object> attributes = new HashMap<>();
        HttpSessionIdHandshakeInterceptor interceptor = new HttpSessionIdHandshakeInterceptor();
        interceptor.beforeHandshake(request,response,wsHandler,attributes);

        Assert.assertEquals(attributes.get("tableId"),22);
        Assert.assertEquals(attributes.get("sessionId"),session.getId());
        Assert.assertEquals(attributes.get("userId"),user.getId());
    }

    @Test
    public void beforeHandshakeWithSpecialHeader() throws Exception {
        headers.add("Sec-WebSocket-Extensions","");
        Map<String,Object> attributes = new HashMap<>();
        HttpSessionIdHandshakeInterceptor interceptor = new HttpSessionIdHandshakeInterceptor();
        interceptor.beforeHandshake(request,response,wsHandler,attributes);

        Assert.assertEquals(headers.get("Sec-WebSocket-Extensions").get(0),"permessage-deflate");
        Assert.assertEquals(attributes.get("tableId"),22);
        Assert.assertEquals(attributes.get("sessionId"),session.getId());
        Assert.assertEquals(attributes.get("userId"),user.getId());
    }

    @Test
    public void afterHandshake() throws Exception {
        HttpSessionIdHandshakeInterceptor interceptor = new HttpSessionIdHandshakeInterceptor();
        interceptor.afterHandshake(request,response,wsHandler,null);
    }

}