package com.ecnu.trivia.controller;



import com.ecnu.trivia.dto.Result;
import com.ecnu.trivia.model.User;
import com.ecnu.trivia.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by joy12 on 2017/12/3.
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/index")
    public String toIndex(){
        return "index";
    }

    /**
     * 注册页面
     */
    @RequestMapping(value = "/signupPage",method = RequestMethod.GET)
    public String signupPage() {
        return "signup";
    }

    /**
     * 注册
     */
    @RequestMapping(value = "/signup",method = RequestMethod.POST)
    @ResponseBody
    public Result signup(User user, HttpServletRequest httpServletRequest) {
        Result result=new Result();
        User signupUser=userService.signup(user);
        if(signupUser==null){
            result.setSuccess(false);
            result.setError("用户名已存在！");
            System.out.println("用户名已存在！");
        }
        else{
            HttpSession session=httpServletRequest.getSession();
            session.setAttribute("user",signupUser);
            result.setSuccess(true);
        }
        return result;
    }
    /**
     * 登录界面
     */
    @RequestMapping(value = "/loginPage",method = RequestMethod.GET)
    public String loginPage() {
        return "login";
    }

    /**
     * 登录
     */
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    @ResponseBody
    public Result login(User user,HttpServletRequest httpServletRequest) {
        User loginUser=userService.login(user);
        if(loginUser==null){
          return new Result(false);
        }
        else {
            HttpSession session=httpServletRequest.getSession();
            session.setAttribute("user",loginUser);
            return new Result(true);
        }
    }

    /**
     * 记录胜率、升级
     */
    @RequestMapping(value = "/recordGameResult",method = RequestMethod.POST)
    @ResponseBody
    public Result record(@RequestParam(value = "isWinner") boolean isWinner, HttpSession session) {
        Result result = new Result();
        User user = (User) session.getAttribute("user");
        if (userService.countWinLose(user,isWinner)){
            result.setSuccess(true);
        } else {
            result.setSuccess(false);
            result.setError("记录胜率失败");
        }
        return result;
    }

}
