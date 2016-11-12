package com.company.interview.mutilthread;

import java.util.concurrent.Callable;

/**
 * Created by Administrator on 9/1/2016.
 */
public class TestCallable1 implements Callable<String> {
    @Override
    public String call() throws Exception {
        String a = "";
        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            System.out.println(a);
            a = String.valueOf(i);
        }
        return a;
    }
}
