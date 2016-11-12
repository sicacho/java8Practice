package com.company.interview.mutilthread;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Administrator on 9/1/2016.
 */
public class Main {
    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(2);
        // set of Callable types
        Set<Callable<String>> callables = new HashSet<Callable<String>>();
        // add tasks to Set
        callables.add(new TestCallable1());
        callables.add(new TestCallable1());
        // list of Future<String> types stores the result of invokeAll()
        List<Future<String>> futures = null;
        try {
            futures = service.invokeAll(callables);
            for(Future<String> future : futures) {
                System.out.println("future : " + future.get());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Map<String,Integer> a = new HashMap<>();
        // iterate through the list and print results from get();

    }
}
