package com.company.stream;

import com.company.lambda.People;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<People> peoples = new ArrayList<People>();

        for(int i = 0 ; i < 100 ; i++) {
            peoples.add(new People(i,"khang"+i));
        }
//        List<Optional<Child>> childList =
                peoples.stream().
                filter(p -> p.id%2==0).
                sorted(People::comparePeople).
                map(People::convertToChild).forEach(System.out::println);
    }
}