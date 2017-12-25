package com.ecnu.trivia.dto;


import com.ecnu.trivia.model.Question;
import com.ecnu.trivia.model.User;
import com.ecnu.trivia.websocket.WebSocketServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

// Game控制了玩家的顺序
// 提问题的的时候会把Question广播，玩家是否答对由前台js判断，ajax传是否答对的boolean，game进行对应操作
// 调用了boardcast的函数：startGame() - 广播游戏初始状态
//                       roll() - 广播当前骰子掷出来的数字 和 分配到的问题。在禁闭室里掷到偶数时，问题为null
//                       answerCorrectly()、answerWrong() - 广播更新所有玩家的信息（位置、金币、下一个是谁等）
public class Game {
    public static final int NUMBER_OF_GOLD_COINS_TO_WON_AND_GAME_OVER = 6;
    public static final int MAX_NUMBER_OF_BYTES_WRITING_TO_ONE_FILE = 10000000;
    public static final int NUMBER_OF_FILES_TO_USE = 1;
    public static final int NUMBER_OF_NEEDED_PLAYER = 2;//by j：游戏开始所必须的玩家数

    private final QuestionMaker questionMaker = new QuestionMaker();

    private ArrayList<Player> players = new ArrayList<Player>();

    private int currentPlayer = -1;//当前轮到的player序号

    private static Logger logger = Logger.getLogger("kata.trivia.Game");
    private static FileHandler fileHandler = null;

    // by j：游戏状态 ：-1-游戏结束 0-游戏准备阶段  1-游戏开始 2-显示骰子点数和问题 3-回答正确 4-回答错误
    private int status = 0;
    // by j：桌号
    private int tableId;
    // by j：一个bean，用来装游戏进行中，通过websocket更新的游戏状态
    private GameStatus gameStatus = null;
    // by j：用来给别的websocket发广播的，总控websocket
    private WebSocketServer gameSocket = null;

    public Game(int tableId) {
        this.tableId = tableId;
        gameSocket = new WebSocketServer();
        gameSocket.addTable(this);
        gameStatus = new GameStatus(this);
        logToAFile();
    }

    /* setters and getters */
    public int getCurrentPlayerId() {
        if (currentPlayer > 0) {
            return players.get(currentPlayer).getUser().getId();
        } else {
            return currentPlayer;
        }

    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public Integer getTableId() {
        return tableId;
    }

    public int getStatus() {
        return status;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }


    /**
     * by j: 添加玩家，增加了user参数
     */
    public void add(String playerName, User user, int initialPlace) {
        players.add(new Player(playerName, user, initialPlace));
        boardcast(gameStatus.toString());
        logger.info(playerName + " was added");
        logger.info("The total amount of players is " + players.size());
    }

    /**
     * by j: 删除一个玩家
     */
    public void remove(int userId) {
        for (Player player : players) {
            if (player.getUser().getId() == userId) {
                players.remove(player);
                boardcast(gameStatus.toString());
            }
        }
        logger.info(userId + " was exit before game start");
        logger.info("The total amount of players is " + players.size());
    }

    /**
     * by j: 玩家准备
     */
    public void setReady(Integer userId) {
        for (Player player : players) {
            if (player.getUser().getId() == userId) {
                player.setIsReady(true);
                logger.info(player.getUser().getUsername() + " was ready");
                boardcast(gameStatus.toString());
                return;
            }
        }
        logger.info(userId + " was not found!");
    }

    /**
     * by j: 每当页面上，该轮玩家按下暂停骰子时，ajax会一直调用到这个函数
     * roll一次的流程：
     * 1、骰子有了一个点数（新的一轮开始）
     * 2、根据点数有了一个问题，带答案
     * ----  boardcast 1&2  ------
     * <p>
     * （后面的这些不是这里做）
     * ----  玩家ajax上传自己回答的是否正确（前台js判断）-----
     * 3、更新所有玩家的位置和状态
     * 4、currentPlayer变为下一个人
     * ----  boardcast 3&4  ------
     * <p>
     * 当玩家在禁闭室里，直到他掷出奇数为止，他就一直在禁闭室里
     *
     * @param rollingNumber 掷骰子掷到了几
     * @return 这个格子的问题，由子方法返回，如果仍在禁闭室内，返回null
     */
    public void roll(int rollingNumber) {
        gameStatus.setDice(rollingNumber);
        gameStatus.setStatus(2);

        logger.info(players.get(currentPlayer) + " is the current player");
        logger.info("They have rolled a " + rollingNumber);

        // 若当前玩家不在禁闭室里，则前进
        if (!players.get(currentPlayer).isInPenaltyBox()) {
            currentPlayerMovesToNewPlaceAndAnswersAQuestion(rollingNumber);
            boardcast(gameStatus.toString());
            return;
        }

        // 若当前玩家在禁闭室里，则判断本轮是否有前进的机会
        //奇数为true，可前进rollingNumber步
        boolean isRollingNumberForGettingOutOfPenaltyBox = (rollingNumber % 2 == 1);
        //boolean isRollingNumberForGettingOutOfPenaltyBox = rollingNumber != 4;

        if (isRollingNumberForGettingOutOfPenaltyBox) {
            players.get(currentPlayer).getOutOfPenaltyBox();
            logger.info(players.get(currentPlayer) + " is getting out of the penalty box");
            currentPlayerMovesToNewPlaceAndAnswersAQuestion(rollingNumber);
            boardcast(gameStatus.toString());
            return;
        }

        // 禁闭室里的玩家掷到偶数，下一轮仍然在禁闭室，没有问题提出
        logger.info(players.get(currentPlayer) + " is not getting out of the penalty box");
        gameStatus.setCurrentQuestion(null);
        players.get(currentPlayer).sentToPenaltyBox();
        nextPlayer();
        //前台注意！若currentQuestion是null，说明当前还在禁闭室里。不用做上传答案的ajax。
        //这个时候currentPlayer已经是下一轮的玩家了，直接做下一轮暂停骰子
        boardcast(gameStatus.toString());

    }

    /**
     * by j： 修改返回值
     * roll()调用，玩家前进并回答问题
     *
     * @param rollingNumber
     * @return 这个格子的问题
     */
    private Question currentPlayerMovesToNewPlaceAndAnswersAQuestion(int rollingNumber) {
        players.get(currentPlayer).moveForwardSteps(rollingNumber);

        logger.info(players.get(currentPlayer)
                + "'s new location is "
                + players.get(currentPlayer).getPlace());
        logger.info("The category is " + players.get(currentPlayer).getCurrentCategory());
        return askQuestion();
    }

    /**
     * by j:修改调用方法，而且不应该用==比较，添加返回值，方便前台显示
     * 提出（打印）一个问题（测试时直接打印）
     */
    private Question askQuestion() {
        Question question = null;
        if (players.get(currentPlayer).getCurrentCategory().equals("Pop")) {
            question = questionMaker.distributePopQuestion();
        }
        if (players.get(currentPlayer).getCurrentCategory().equals("Science")) {
            question = questionMaker.distributeScienceQuestion();
        }
        if (players.get(currentPlayer).getCurrentCategory().equals("Sports")) {
            question = questionMaker.distributeSportsQuestion();
        }
        if (players.get(currentPlayer).getCurrentCategory().equals("Rock")) {
            question = questionMaker.distributeRockQuestion();
        }


        if (question != null) {
            logger.info(question.getTitle());
        } else {
            logger.info("没有该类问题");
            question = new Question("没有该类问题,继续游戏请传null给本对象判断函数");
        }

        gameStatus.setCurrentQuestion(question);
        return question;
    }

    // TODO-later: The name of method Game.wasCorrectlyAnswered() should be Game.answeredCorrectly()

    /**
     * 玩家回答正确，赢得金币
     * or 玩家正在关禁闭，直接下一个人
     */
    public void answeredCorrectly() {
        //只有出错的情况会调这个分支
        if (players.get(currentPlayer).isInPenaltyBox()) {
            nextPlayer();
            boardcast("error");
            return;
        }
        if (currentPlayerGetsAGoldCoinAndSelectNextPlayer()) {
            //还没有人赢，只更新位置和分数
            gameStatus.setStatus(3);
            boardcast(gameStatus.toString());
        } else {
            //有人赢了，结算结果并更新游戏状态
            endGame();
            boardcast(gameStatus.toString());
        }
    }


    /**
     * answeredCorrectly调用，分配金币
     *
     * @return 游戏是否仍继续
     */
    private boolean currentPlayerGetsAGoldCoinAndSelectNextPlayer() {

        logger.info("Answer was correct!!!!");
        players.get(currentPlayer).winAGoldCoin();

        logger.info(players.get(currentPlayer)
                + " now has "
                + players.get(currentPlayer).countGoldCoins()
                + " Gold Coins.");

        boolean isGameStillInProgress = isGameStillInProgress();
        nextPlayer();

        return isGameStillInProgress;
    }

    /**
     * by j：切换下一个玩家
     */
    private void nextPlayer() {
        currentPlayer++;
        if (currentPlayer == players.size()) currentPlayer = 0;
        gameStatus.setCurrentPlayerIndex(currentPlayer);
        gameStatus.setCurrentPlayerId(players.get(currentPlayer).getUser().getId());
    }

    // TODO-later: The name of method Game.wrongAnswer() should be Game.answeredWrong()

    /**
     * by j：玩家回答错误，被关进禁闭室
     */
    public void answeredWrong() {
        logger.info("Question was incorrectly answered");
        logger.info(players.get(currentPlayer) + " was sent to the penalty box");

        players.get(currentPlayer).sentToPenaltyBox();
        nextPlayer();
        gameStatus.setStatus(4);
        boardcast(gameStatus.toString());
    }

    /**
     * 判断是否已有人获胜
     *
     * @return 游戏是否仍要继续
     */
    private boolean isGameStillInProgress() {
        return !(players.get(currentPlayer).countGoldCoins() == NUMBER_OF_GOLD_COINS_TO_WON_AND_GAME_OVER);
    }

    /**
     * by j：判断游戏是否正在进行
     *
     * @return 游戏是否仍要继续
     */
    public boolean isGameStart() {
        return status == 1;
    }

    /**
     * by j：判断游戏人数是否够了
     *
     * @return
     */
    public boolean isEnoughPlayer() {
        return players.size() == NUMBER_OF_NEEDED_PLAYER;
    }

    /**
     * by j：判断是否所有玩家都准许开始游戏
     *
     * @return
     */
    public boolean isAllPlayerReady() {
        boolean result = (players.size() > 0);
        for (Player player : players) {
            result &= player.getIsReady();
        }
        return result;
    }

    /**
     * by j：装载问题
     *
     * @param popQuestions
     * @param scienceQuestions
     * @param sportsQuestions
     * @param rockQuestions
     */
    public void prepareQuestions(List<Question> popQuestions, List<Question> scienceQuestions, List<Question> sportsQuestions, List<Question> rockQuestions) {
        questionMaker.addPopQuestionList(popQuestions);
        questionMaker.addScienceQuestionList(scienceQuestions);
        questionMaker.addSportsQuestionList(sportsQuestions);
        questionMaker.addRockQuestionList(rockQuestions);
    }


    /**
     * by j：开始游戏
     *
     * @return 游戏是否成功开始
     */
    public boolean startGame() {
        if (boardcast("start") == 0) {
            status = 1;
            currentPlayer = 0;
            gameStatus.setCurrentPlayerId(players.get(currentPlayer).getUser().getId());
            gameStatus.setCurrentPlayerIndex(currentPlayer);
            gameStatus.setStatus(status);
            boardcast(gameStatus.toString());
            gameStatus.setFirstRound(false);
            return true;
        } else {
            return false;
        }
    }

    /**
     * by j：结束游戏并结算
     */
    public void endGame() {
        logger.info("--------------------游戏结束，开始结算--------------------");
        status = -1;
        gameStatus.setStatus(status);
        Player winner = null;
        for (Player player : players) {
            logger.info(player.toString() + " : " + player.countGoldCoins() + " 个金币");
            if (player.countGoldCoins() == 6) {
                winner = player;
            }
        }
        gameStatus.setWinner(winner);
        boardcast(gameStatus.toString());
        gameSocket.removeTable(tableId);
        // do something to database
        logger.info("--------------------游戏结束，结算完毕--------------------");
    }

    /**
     * by j 给桌上的每个玩家发一条消息
     *
     * @param msg
     * @return result为发送消息失败的用户个数
     */
    private int boardcast(String msg) {
        int result = 0;
        for (Player player : players) {
            if (!gameSocket.sendMessageToUser(player.getUser().getId(), msg)) {
                result++;
            }
        }
        return result;
    }

    /**
     * by j 给指定的某个玩家发一条消息
     * 目前没用到，先放着。。。
     *
     * @param msg
     * @return 是否发送成功
     */
    private boolean unicast(Player target, String msg) {
        return gameSocket.sendMessageToUser(target.getUser().getId(), msg);
    }


    /* log */
    private void logToAFile() {
        try {
            fileHandler = new FileHandler("%h/Game-logging.log"
                    , MAX_NUMBER_OF_BYTES_WRITING_TO_ONE_FILE
                    , NUMBER_OF_FILES_TO_USE, true);
            fileHandler.setFormatter(new SimpleFormatter());
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.addHandler(fileHandler);
    }

    /**
     * 修改记录：
     * 2017.12.24 by.jcy
     * 新增确定玩家是否在桌上的函数
     */
    public boolean hasPlayer(int userId){
        for (Player player:players) {
            if (player.getUser().getId() == userId){
                return true;
            }
        }
        return false;
    }

}
