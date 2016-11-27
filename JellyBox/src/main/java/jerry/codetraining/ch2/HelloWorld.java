package jerry.codetraining.ch2;

import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Created by jerryDev on 2016. 11. 24..
 */
public class HelloWorld {
    public static void main(String[] args) {
        Function<InputStream,String> inputStreamStringFunction = (InputStream is )-> new Scanner( is ).next();
        printline("What is your name?",System.out::println);
        // input
        String name = inputLine(System.in, inputStreamStringFunction);
        // process

        String result = String.format("Hello, %s, nice to meet you!",name);
        // output
        printline( result,System.out::println );
    }
    private static void printline(String s, Consumer<String> consumer) {
        consumer.accept( s );
    }
    private static String inputLine(InputStream in, Function<InputStream, String> func) {
        return func.apply( in );
    }
}
