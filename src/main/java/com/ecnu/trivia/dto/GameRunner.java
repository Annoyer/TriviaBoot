package com.ecnu.trivia.dto;



import com.ecnu.trivia.model.Question;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class GameRunner {
    private static boolean isGameStillInProgress;
    //之前测试用的
//    private static User jungkook = new User("Jungkook");
//    private static User jimin = new User("Jimin");
//    private static User jin = new User("Jin");
//    private static User j = new User("J");

    private static List<Question> popQuestions = new LinkedList<Question>();
    private static List<Question> scienceQuestions = new LinkedList<Question>();
    private static List<Question> sportsQuestions = new LinkedList<Question>();
    private static List<Question> rockQuestions = new LinkedList<Question>();

    /**
     * 改完代码，但是没加websocket之前用来测试的，程序逻辑应该没有大问题
     * @param args
     */
    public static void main(String[] args) {

//        loadQuestion();

//        Game aGame = new Game();
//
//        aGame.add(jungkook.getUsername(),jungkook);
//        aGame.add(jimin.getUsername(),jimin);
//        aGame.add(jin.getUsername(),jin);
//        aGame.add(j.getUsername(),j);
//
//        Random rand = new Random();
//
//        if (!aGame.isGameStart()){
//            aGame.prepareQuestions(popQuestions,scienceQuestions,sportsQuestions,rockQuestions);
//            aGame.startGame();
//        }
//
//        do {
//            Question currentQ = aGame.roll(rand.nextInt(5) + 1);
//            if (currentQ==null){
//                System.out.println("哦你在禁闭室里");
//                continue;
//            } else {
//                System.out.println("GameRunner :: " + currentQ.getQ());
//                if (currentQ.isCorrectAnswer(scannerIn())){
//                    isGameStillInProgress = aGame.answeredCorrectly();
//                } else {
//                    isGameStillInProgress = aGame.answeredWrong();
//                }
//            }
//        } while (isGameStillInProgress);
//
//        aGame.endGame();

    }

//    public static void loadQuestion(){
//        for (int i=0; i<10; i++){
//            popQuestions.add(new Question("pop"+i,"pop"+i));
//            scienceQuestions.add(new Question("science"+i,"science"+i));
//            sportsQuestions.add(new Question("sports"+i,"sports"+i));
//            rockQuestions.add(new Question("rock"+i,"rock"+i));
//        }
//
//    }

    public static String scannerIn(){
        Scanner sc = new Scanner(System.in);
        String result = sc.nextLine();  //读取字符串型输入
        return result;
    }
}
