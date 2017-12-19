package com.ecnu.trivia.util;

import java.util.Random;

/**
 * Created by joy12 on 2017/12/9.
 */
public class GameUtil {
    public static int runDice(){
        Random rand = new Random();
        return (rand.nextInt(5)+1);
    }

}
