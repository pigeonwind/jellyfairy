package jerry.codetraining.ch2;

import java.util.Scanner;

/**
 * Created by jerryDev on 2016. 11. 24..
 */
public class HelloWorldNoValVersion {
    public static void main(String[] args) {
        System.out.println("What is your name?");
        System.out.println(String.format("Hello, %s, nice to meet you!",new Scanner( System.in).next()));
    }
}
