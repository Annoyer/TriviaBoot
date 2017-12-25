package com.ecnu.trivia.controller;


import com.ecnu.trivia.dto.Player;
import com.ecnu.trivia.dto.Result;
import com.ecnu.trivia.model.User;
import com.ecnu.trivia.service.GameService;
import com.ecnu.trivia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by joy12 on 2017/12/9.
 * game**所有路径都要做登录验证拦截
 */
@Controller
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameService gameService;
    @Autowired
    private UserService userService;

    /**
     * @return 所有游戏桌页面
     * ok
     */
    @RequestMapping(value = "/tables")
    public ModelAndView toTables() {
        ModelAndView mv = new ModelAndView("tables");
        mv.addObject("tables", gameService.getAllTables());
        return mv;
    }

    /**
     * @param tableId 桌号
     * @return 进入游戏页面
     * 修改记录：
     * 2017.12.24 by.jcy
     *      配合service的逻辑，若当前用户不在桌上（不是通过选桌页面进来的），重定向回选桌页面
     */
    @RequestMapping(value = "/gamePage")
    public ModelAndView toGamePage(@RequestParam("tableId") Integer tableId) {
        ModelAndView mv = new ModelAndView("game_page");
        mv.addObject("tableId", tableId);
        return mv;
    }

    /**
     * @param tableId 桌号
     * @return 进入游戏页面
     * 修改记录：
     * 2017.12.24 by.jcy
     *      配合service的逻辑，若当前用户不在桌上（不是通过选桌页面进来的），重定向回选桌页面
     */
    @RequestMapping(value = "/loadGameInfo")
    @ResponseBody
    public Result loadGameInfo(@RequestParam("tableId") Integer tableId, HttpSession session) {
        Result result = new Result();
        User user = (User) session.getAttribute("user");
        List<Player> players = gameService.getPlayersByTable(tableId,user);
        if (players.size()==1 && players.get(0).getPlace() == -1){
            result.setSuccess(false);
            result.setError("请先选择您的位置");
        } else {
            result.setSuccess(true);
            result.setData(players);
        }
        return result;
    }

    /**
     * 玩家选桌
     *
     * @param tableId 桌号
     * @param session 获取userId
     * @return 若result为success，则在进入游戏桌页面时建立websocket连接
     * 否则，alert data.error
     */
    @RequestMapping(value = "/chooseTable", method = RequestMethod.POST)
    @ResponseBody
    public Result chooseTable(@RequestParam("tableId") int tableId, @RequestParam("initialPlace") int initialPlace, HttpSession session) {
        Result result = new Result();
        User user = (User) session.getAttribute("user");
//        if (user==null){
//            user = new User("user1");
//            user.setId(1);
//            session.setAttribute("user",user);
//        }
        if (gameService.userChooseTable(tableId, user, initialPlace)) {
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
            result.setError("很抱歉，该桌已满员");
        }
        return result;
    }

    /**
     * 玩家按下ready按钮，表示准许游戏开始
     *
     * @param tableId 桌号
     */
    @RequestMapping(value = "/setReady", method = RequestMethod.POST)
    @ResponseBody
    public void setPlayerReady(@RequestParam("tableId") Integer tableId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        gameService.setPlayerReady(tableId, user.getId());
    }

    /**
     * 玩家按下暂停骰子按钮
     *c:骰子点数改为从前台传入
     * @param tableId 桌号
     */
    @RequestMapping(value = "/stopDice")
    @ResponseBody
    public void stopDice(@RequestParam("tableId") Integer tableId,@RequestParam("num") Integer num) {
        gameService.stopDice(tableId,num);
    }

    /**
     * 玩家上传自己的答案是否正确
     *
     * @param tableId   桌号
     * @param isCorrect 答案是否正确
     */
    @RequestMapping(value = "/answerQuestion")
    @ResponseBody
    public void answerQuestion(@RequestParam("tableId") Integer tableId, @RequestParam("isCorrect") boolean isCorrect) {
        gameService.answerQuestion(tableId, isCorrect);
    }
}
