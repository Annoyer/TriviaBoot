package com.ecnu.trivia.dto;

import com.ecnu.trivia.model.User;
import com.ecnu.trivia.websocket.WebSocketServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

/**
 * Created by joy12 on 2017/12/27.
 */
public class PlayerTest {

    User user;

    @Before
    public void setUp() throws Exception {
        user = new User(5,"jjj","j",1,0,0);
    }

    @Test
    public void setIsReady() throws Exception {
        Player player = new Player(user.getUsername(),user,0);

        Assert.assertTrue(!player.getIsReady());
        player.setIsReady(true);
        Assert.assertTrue(player.getIsReady());
    }

    @Test
    public void getIsReady() throws Exception {
        Player player = new Player(user.getUsername(),user,0);
        Field field = player.getClass().getDeclaredField("isReady");
        field.setAccessible(true);

        field.set(player,true);
        Assert.assertEquals(player.getIsReady(),field.get(player));
        field.set(player,false);
        Assert.assertEquals(player.getIsReady(),field.get(player));
    }

    @Test
    public void getUser() throws Exception {
        Player player = new Player(user.getUsername(),user,0);
        Field field = player.getClass().getDeclaredField("user");
        field.setAccessible(true);

        Assert.assertEquals(player.getUser(),user);
        Assert.assertEquals(player.getUser(),field.get(player));
    }

    @Test
    public void moveForwardSteps() throws Exception {
        Player player = new Player(user.getUsername(),user,0);

        Assert.assertEquals(player.getPlace(),0);

        player.moveForwardSteps(Player.MAX_NUMBER_OF_PLACE-1);
        Assert.assertEquals(Player.MAX_NUMBER_OF_PLACE-1,player.getPlace());

        player.moveForwardSteps(2);
        Assert.assertEquals(1,player.getPlace());

    }

    @Test
    public void getPlace() throws Exception {
        Player player = new Player(user.getUsername(),user,0);
        Field field = player.getClass().getDeclaredField("place");
        field.setAccessible(true);

        field.set(player,1);
        Assert.assertEquals(player.getPlace(),field.get(player));
        field.set(player,5);
        Assert.assertEquals(player.getPlace(),field.get(player));
    }

    @Test
    public void getCurrentCategory() throws Exception {
        Player player = new Player(user.getUsername(),user,0);
        for (int i=0; i<Player.MAX_NUMBER_OF_PLACE; i++){
            String type = null;
            switch (player.getPlace()){
                case Player.CATEGORY_POP_1:
                case Player.CATEGORY_POP_2:
                case Player.CATEGORY_POP_3: type = Player.POP; break;

                case Player.CATEGORY_SCIENCE_1:
                case Player.CATEGORY_SCIENCE_2:
                case Player.CATEGORY_SCIENCE_3: type = Player.SCIENCE; break;

                case Player.CATEGORY_SPORTS_1:
                case Player.CATEGORY_SPORTS_2:
                case Player.CATEGORY_SPORTS_3: type = Player.SPORTS; break;
            }
            if (type==null && player.getPlace()>=0 && player.getPlace()<Player.MAX_NUMBER_OF_PLACE){
                type = Player.ROCK;
            }
            String result = player.getCurrentCategory();
            Assert.assertNotNull(result);
            Assert.assertTrue(result.equals(type));
        }
    }

    @Test
    public void winAGoldCoin() throws Exception {
        Player player = new Player(user.getUsername(),user,0);
        int oldCount = player.getSumOfGoldCoins();
        player.winAGoldCoin();
        Assert.assertEquals(oldCount+1,player.getSumOfGoldCoins());
    }

    @Test
    public void countGoldCoins() throws Exception {
        Player player = new Player(user.getUsername(),user,0);
        Field field = player.getClass().getDeclaredField("sumOfGoldCoins");
        field.setAccessible(true);

        field.set(player,1);
        Assert.assertEquals(player.getSumOfGoldCoins(),field.get(player));
        field.set(player,5);
        Assert.assertEquals(player.getSumOfGoldCoins(),field.get(player));
    }

    @Test
    public void isInPenaltyBox() throws Exception {
        Player player = new Player(user.getUsername(),user,0);
        Field field = player.getClass().getDeclaredField("isInPenaltyBox");
        field.setAccessible(true);

        Assert.assertEquals(player.isInPenaltyBox(),false);
        field.set(player,true);
        Assert.assertEquals(player.isInPenaltyBox(),field.get(player));
        field.set(player,false);
        Assert.assertEquals(player.isInPenaltyBox(),field.get(player));
    }

    @Test
    public void getOutOfPenaltyBox() throws Exception {
        Player player = new Player(user.getUsername(),user,0);

        player.sentToPenaltyBox();

        player.getOutOfPenaltyBox();
        Assert.assertEquals(player.isInPenaltyBox(),false);
    }

    @Test
    public void sentToPenaltyBox() throws Exception {
        Player player = new Player(user.getUsername(),user,0);
        Assert.assertEquals(player.isInPenaltyBox(),false);
        player.sentToPenaltyBox();
        Assert.assertEquals(player.isInPenaltyBox(),true);
    }

}