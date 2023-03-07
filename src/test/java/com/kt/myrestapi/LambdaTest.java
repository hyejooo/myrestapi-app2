package com.kt.myrestapi;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Consumer;

public class LambdaTest {
    @Test
    public void consumer() {
        List<String> list = List.of("aa", "bb", "cc"); // Immutable List
        // 1. Anonymous Inner class
        Consumer<String> action = new Consumer<>() {
            @Override
            public void accept(String s) {
                System.out.println("s = " + s);
            }
        };
        // 2. Lambda Expression
        // Consumer의 추상 메서드 void accept(T, t)
        list.forEach(val -> System.out.println(val));
        // 3. Method Reference
        list.forEach(System.out::println);
    }

    @Test
    public void runnable() {

        // 1. Inner class
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Anonymous Inner class");
            }
        });
        t1.start();
        //2. Lambda Expression
        Thread t2 = new Thread(() -> System.out.println("Lambda Expression"));
        t2.start();
    }
}
