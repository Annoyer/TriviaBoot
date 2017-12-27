package com.ecnu.trivia.controller;

import com.ecnu.trivia.BaseTest;
import com.ecnu.trivia.dto.Player;
import com.ecnu.trivia.dto.Result;
import com.ecnu.trivia.model.User;
import com.ecnu.trivia.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by joy12 on 2017/12/25.
 */
//@AutoConfigureMockMvc
public class GameControllerTest extends BaseTest {

//    @Autowired
    //controller层要用MockMvc
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext wac;

    @InjectMocks
    private GameController gameController;
    @Mock
    private GameService gameService;

    private MockHttpSession session = new MockHttpSession();


    @Before
    public void setUp() throws Exception {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver(); //配置视图解析器
        resolver.setPrefix("/");
        resolver.setSuffix(".ftl");

        User user = new User(5,"jjj","j",1,0,0);
        session.setAttribute("user",user);

        mvc = MockMvcBuilders.standaloneSetup(gameController).setViewResolvers(resolver).build();
        //mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void toTables() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/game/tables")
                                                .session(session);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("tables"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void getAllTableInfo() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/game/getAllTables")
                .session(session);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void toGamePage() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/game/gamePage")
                                                .session(session)
                                                .param("tableId","0");
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("game_page"))
                .andExpect(MockMvcResultMatchers.model().attribute("tableId",0))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void loadGameInfoAfterEnterGamePageFromChooseTable() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/game/loadGameInfo")
                .param("tableId","0")
                .session(session);

        List<Player> playerList = new ArrayList<>();
        Result successResult = new Result(true);
        successResult.setData(playerList);

        when(gameService.getPlayersByTable(0,(User)(session.getAttribute("user")))).thenReturn(playerList);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(successResult)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void loadGameInfoAfterEnterGamePageNotFromChooseTable() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/game/loadGameInfo")
                .session(session)
                .param("tableId","0");

        List<Player> playerList = new ArrayList<>();
        playerList.add(0,new Player(null,null,-1));
        Result failResult = new Result(false);
        failResult.setError("请先选择您的位置");

        when(gameService.getPlayersByTable(0,(User) session.getAttribute("user"))).thenReturn(playerList);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(failResult)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void chooseTableSuccess() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/game/chooseTable")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .param("tableId","0")
                .param("initialPlace","0");

        Result successResult = new Result(true);
        when(gameService.userChooseTable(0,(User) session.getAttribute("user"),0)).thenReturn(true);
        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(successResult)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void chooseTableFail() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/game/chooseTable")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .param("tableId","0")
                .param("initialPlace","0");

        when(gameService.userChooseTable(0,(User) session.getAttribute("user"), 0)).thenReturn(false);

        Result failResult = new Result(false);
        failResult.setError("很抱歉，该桌已满员");

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json(objectMapper.writeValueAsString(failResult)))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();
    }

    @Test
    public void setPlayerReady() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/game/setReady")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .param("tableId","0");


        mvc.perform(request);

        User user = (User) session.getAttribute("user");
        verify(gameService).setPlayerReady(0,user.getId());
    }

    @Test
    public void stopDice() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/game/stopDice")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .session(session)
                .param("tableId","0")
                .param("num","1");

        mvc.perform(request);

        verify(gameService).stopDice(0,1);
    }

    @Test
    public void answerQuestionCorrect() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/game/answerQuestion")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("tableId","0")
                .param("isCorrect","true");

        mvc.perform(request);

        verify(gameService).answerQuestion(0,true);
    }

    @Test
    public void answerQuestionFalse() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/game/answerQuestion")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("tableId","0")
                .param("isCorrect","false");

        mvc.perform(request);

        verify(gameService).answerQuestion(0,false);
    }

}