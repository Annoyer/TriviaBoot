package com.ecnu.trivia.dto;

import com.ecnu.trivia.model.User;
import com.ecnu.trivia.model.Question;
import com.ecnu.trivia.websocket.WebSocketServer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;

/**
 * Created by joy12 on 2017/12/27.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({WebSocketServer.class})
public class GameTest{

    List<User> users;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(WebSocketServer.class);
        PowerMockito.when(WebSocketServer.sendMessageToUserInGame(anyInt(),anyString())).thenReturn(true);
        PowerMockito.when(WebSocketServer.broadcastToUserInTablesPage()).thenReturn(true);
        users = new ArrayList<>();
        users.add(new User(5,"jjj","j",1,0,0));
        users.add(new User(6,"jjj","junkook",1,0,0));
        users.add(new User(4,"1","c",1,0,0));
        users.add(new User(3,"123","caomiao",1,0,0));
    }

    @Test
    public void getCurrentPlayerIdBeforeStart() throws Exception {
        Game game = new Game(0);

        Assert.assertEquals(game.getCurrentPlayerId(), -1);

        game.add(users.get(0).getUsername(),users.get(0),0);

        Assert.assertEquals(game.getCurrentPlayerId(), -1);
    }

    @Test
    public void getCurrentPlayerIdAfterStart() throws Exception {
        Game game = new Game(0);
        for (User user: users) {
            game.add(user.getUsername(),user,0);
        }

        Field field = Game.class.getDeclaredField("currentPlayer");
        field.setAccessible(true);
        field.set(game,1);
        Assert.assertTrue(game.getCurrentPlayerId() == game.getPlayers().get(1).getUser().getId());
        field.set(game,2);
        Assert.assertTrue(game.getCurrentPlayerId() == game.getPlayers().get(2).getUser().getId());
    }

    @Test
    public void getPlayers() throws Exception {
        Game game = new Game(0);
        for (User user: users) {
            game.add(user.getUsername(),user,0);
        }

        List<Player> players = game.getPlayers();
        Assert.assertTrue(players.size() == users.size());
        for (Player player : players){
            Assert.assertTrue(users.contains(player.getUser()));
        }
    }

    @Test
    public void getTableId() throws Exception {
        Game game = new Game(3);
        Assert.assertTrue(game.getTableId() == 3);
    }

    @Test
    public void getStatus() throws Exception {
        Game game = new Game(0);
        Assert.assertTrue(game.getStatus() == 0);

        Field field = Game.class.getDeclaredField("status");
        field.setAccessible(true);

        field.set(game,1);
        Assert.assertTrue(game.getStatus() == 1);
        field.set(game,-1);
        Assert.assertTrue(game.getStatus() == -1);
    }

    @Test
    public void getCurrentPlayer() throws Exception {
        Game game = new Game(0);
        Field field = Game.class.getDeclaredField("currentPlayer");
        field.setAccessible(true);

        field.set(game,1);
        Assert.assertTrue(game.getCurrentPlayer() == 1);
        field.set(game,3);
        Assert.assertTrue(game.getCurrentPlayer() == 3);
    }

    @Test
    public void hasPlayer() throws Exception {
        Game game = new Game(0);
        game.add(users.get(0).getUsername(),users.get(0),0);

        Assert.assertTrue(game.hasPlayer(users.get(0).getId()));
        Assert.assertTrue(!game.hasPlayer(users.get(1).getId()));
    }

    @Test
    public void add() throws Exception {
        Game game = new Game(0);
        game.add(users.get(0).getUsername(),users.get(0),0);

        Assert.assertTrue(game.getPlayers().size() == 1);
        PowerMockito.verifyStatic(atLeast(1));

        game.add(users.get(1).getUsername(),users.get(1),0);

        Assert.assertTrue(game.getPlayers().size() == 2);
        PowerMockito.verifyStatic(atLeast(2));
    }

    @Test
    public void remove() throws Exception {
        Game game = new Game(0);
        game.add(users.get(0).getUsername(),users.get(0),0);
        game.add(users.get(1).getUsername(),users.get(1),0);

        game.remove(users.get(0).getId());
        Assert.assertTrue(game.getPlayers().size() == 1);
        Assert.assertTrue(game.hasPlayer(users.get(1).getId()));
        Assert.assertTrue(!game.hasPlayer(users.get(0).getId()));
        PowerMockito.verifyStatic(atLeast(2));
    }

    @Test
    public void setReady() throws Exception {
        Game game = new Game(0);
        game.add(users.get(0).getUsername(),users.get(0),0);
        game.add(users.get(1).getUsername(),users.get(1),0);

        game.setReady(users.get(0).getId());

        Assert.assertTrue(game.getPlayer(users.get(0).getId()).getIsReady());
        Assert.assertTrue(!game.getPlayer(users.get(1).getId()).getIsReady());
        PowerMockito.verifyStatic(times(3));
    }

    @Test
    public void rollWhenNotInPenaltyBox() throws Exception {
        PowerMockito.when(WebSocketServer.sendMessageToUserInGame(anyInt(),anyString())).thenReturn(true);

        Game readyGame = prepareAGame();
        readyGame.startGame();
        List<Player> players = readyGame.getPlayers();
        for (int i=0; i<players.size(); i++){
            players.get(i).setPlace(0);
        }

        Player currentPlayer = readyGame.getPlayers().get(readyGame.getCurrentPlayer());
        for (int i = 1; i <= 4; i++) {
            readyGame.roll(1);
            Assert.assertTrue(readyGame.getGameStatus().getDice() == 1);
            Assert.assertTrue(readyGame.getGameStatus().getStatus() == 2);
            Assert.assertTrue(currentPlayer.getPlace() == i);
        }
    }

    @Test
    public void rollWhenNotInPenaltyBoxAndDiceIsEven() throws Exception {
        Game readyGame = prepareAGame();
        readyGame.startGame();
        Player currentPlayer = readyGame.getPlayers().get(readyGame.getCurrentPlayer());
        currentPlayer.sentToPenaltyBox();
        int oldPlace = currentPlayer.getPlace();

        readyGame.roll(6);
        Assert.assertTrue(readyGame.getGameStatus().getDice() == 6);
        Assert.assertTrue(readyGame.getGameStatus().getStatus() == 2);
        Assert.assertTrue(currentPlayer.isInPenaltyBox());
        Assert.assertTrue(currentPlayer.getPlace() == oldPlace);
        PowerMockito.verifyStatic();
    }

    @Test
    public void rollWhenNotInPenaltyBoxAndDiceIsOdd() throws Exception {
        Game readyGame = prepareAGame();
        readyGame.startGame();
        Player currentPlayer = readyGame.getPlayers().get(readyGame.getCurrentPlayer());
        currentPlayer.sentToPenaltyBox();

        readyGame.roll(5);
        Assert.assertTrue(readyGame.getGameStatus().getDice() == 5);
        Assert.assertTrue(readyGame.getGameStatus().getStatus() == 2);
        Assert.assertTrue(!currentPlayer.isInPenaltyBox());
        PowerMockito.verifyStatic();
    }

    @Test
    public void answeredCorrectlyWhenNotWin() throws Exception {
        Game readyGame = prepareAGame();
        readyGame.startGame();
        int currentPlayer = readyGame.getCurrentPlayer();
        int currentPlayerId = readyGame.getCurrentPlayerId();
        int currentCoins = readyGame.getPlayer(currentPlayerId).getSumOfGoldCoins();

        readyGame.answeredCorrectly();

        Assert.assertTrue(readyGame.getStatus() == 3);
        Assert.assertTrue(readyGame.getGameStatus().getStatus() == 3);
        Assert.assertTrue(readyGame.getCurrentPlayer() == (currentPlayer+1)%Game.NUMBER_OF_NEEDED_PLAYER);
        Assert.assertTrue(readyGame.getPlayer(currentPlayerId).getSumOfGoldCoins() == currentCoins+1);
        PowerMockito.verifyStatic();
    }

    @Test
    public void answeredCorrectlyWhenWin() throws Exception {
        Game readyGame = prepareAGame();
        readyGame.startGame();
        Player player = readyGame.getPlayers().get(readyGame.getCurrentPlayer());
        player.setSumOfGoldCoins(Game.NUMBER_OF_GOLD_COINS_TO_WON_AND_GAME_OVER-1);

        readyGame.answeredCorrectly();

        Assert.assertTrue(readyGame.getStatus() == -1);
        Assert.assertTrue(readyGame.getGameStatus().getStatus() == -1);
        PowerMockito.verifyStatic();

    }

    @Test
    public void answeredWrongByLastPlayer() throws Exception {
        Game readyGame = prepareAGame();
        readyGame.startGame();

        readyGame.setCurrentPlayer(Game.NUMBER_OF_NEEDED_PLAYER-1);

        readyGame.answeredWrong();

        Assert.assertTrue(readyGame.getStatus() == 4);
        Assert.assertTrue(readyGame.getGameStatus().getStatus() == 4);
        PowerMockito.verifyStatic();
    }

    @Test
    public void isGameStart() throws Exception {
        Game game = new Game(0);
        Assert.assertTrue(!game.isGameStart());

        for (User user: users) {
            game.add(user.getUsername(),user,0);
            game.setReady(user.getId());
            Assert.assertTrue(!game.isGameStart());
        }

        game.startGame();
        Assert.assertTrue(game.isGameStart());

        game.endGame();
        Assert.assertTrue(!game.isGameStart());
    }

    @Test
    public void isEnoughPlayer() throws Exception {

        Game game = new Game(0);

        Assert.assertTrue(users.size() >= Game.NUMBER_OF_NEEDED_PLAYER);

        for (int i=0; i<Game.NUMBER_OF_NEEDED_PLAYER; i++) {
            Assert.assertTrue(!game.isEnoughPlayer());
            game.add(users.get(i).getUsername(),users.get(i),0);
        }
        Assert.assertTrue(game.isEnoughPlayer());
    }

    @Test
    public void isAllPlayerReady() throws Exception {

        Game game = new Game(0);

        Assert.assertTrue(users.size() >= Game.NUMBER_OF_NEEDED_PLAYER && Game.NUMBER_OF_NEEDED_PLAYER > 2);
        Assert.assertTrue(!game.isAllPlayerReady());

        for (int i=0; i<2; i++) {
            game.add(users.get(i).getUsername(),users.get(i),0);
            Assert.assertTrue(!game.isAllPlayerReady());
            game.setReady(users.get(i).getId());
            Assert.assertTrue(game.isAllPlayerReady());
        }

        for (int i=2; i<Game.NUMBER_OF_NEEDED_PLAYER; i++) {
            game.add(users.get(i).getUsername(),users.get(i),0);
            Assert.assertTrue(!game.isAllPlayerReady());
        }
        for (int i=2; i<Game.NUMBER_OF_NEEDED_PLAYER; i++) {
            Assert.assertTrue(!game.isAllPlayerReady());
            game.setReady(users.get(i).getId());
        }
        Assert.assertTrue(game.isAllPlayerReady());

    }


    @Test
    public void startGame() throws Exception {
        Game readyGame = prepareAGame();
        Assert.assertTrue(readyGame.getGameStatus().isFirstRound());
        readyGame.startGame();
        Assert.assertTrue(readyGame.getStatus() == 1);
        Assert.assertTrue(readyGame.getCurrentPlayer() == 0);
        Assert.assertTrue(!readyGame.getGameStatus().isFirstRound());
    }

    @Test
    public void endGameWithNoWinner() throws Exception {
        Game readyGame = prepareAGame();
        readyGame.startGame();
        readyGame.endGame();
        Assert.assertTrue(readyGame.getStatus() == -1);
        Assert.assertTrue(readyGame.getGameStatus().getWinner() == null);
    }

    @Test
    public void endGameWithAWinner() throws Exception {
        Game readyGame = prepareAGame();
        readyGame.startGame();
        Player player = readyGame.getPlayers().get(readyGame.getCurrentPlayer());
        player.setSumOfGoldCoins(Game.NUMBER_OF_GOLD_COINS_TO_WON_AND_GAME_OVER);

        readyGame.endGame();
        Assert.assertTrue(readyGame.getStatus() == -1);
        Assert.assertTrue(readyGame.getGameStatus().getWinner().equals(player));
    }

    private Game prepareAGame(){
        Game readyGame = new Game(1);
        for (User user : users){
            readyGame.add(user.getUsername(),user,0);
            readyGame.setReady(user.getId());
            readyGame.getPlayer(user.getId()).setConnected(true);
        }
        List<Question> questions = new ArrayList<>();
        for (int i=0; i<QuestionMaker.MAX_NUMBER_OF_QUESTIONS; i++){
            questions.add(new Question());
        }
        readyGame.prepareQuestions(questions,questions,questions,questions);

        return readyGame;
    }

}