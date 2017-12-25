package com.ecnu.trivia.controller;

import com.ecnu.trivia.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.*;

/**
 * Created by joy12 on 2017/12/25.
 */
@AutoConfigureMockMvc
public class GameControllerTest extends BaseTest {

    @Autowired
    //controller层要用MockMvc
    private MockMvc mvc;

    @Autowired
    private WebApplicationContext wac;


    @Before
    public void setUp() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void toTables() throws Exception {

    }

    @Test
    public void toGamePage() throws Exception {

    }

    @Test
    public void loadGameInfo() throws Exception {

    }

    @Test
    public void chooseTable() throws Exception {

    }

    @Test
    public void setPlayerReady() throws Exception {

    }

    @Test
    public void stopDice() throws Exception {

    }

    @Test
    public void answerQuestion() throws Exception {

    }

}