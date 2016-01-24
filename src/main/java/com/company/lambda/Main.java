package com.company.lambda;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
	// write your code here
        List<People> peoples = new ArrayList<People>();

        for(int i = 0 ; i < 100 ; i++) {
            peoples.add(new People(i,"khang"+i));
        }

        PrintClass.printPeople(peoples,(People p)  -> p.id%2 == 0 );
        System.out.println("-------------------------");
        PrintClass.printPeople(peoples,(People p) -> p.id%3 == 0);
        System.out.println("Methond reference-------------------------");
        PrintClass.printPeople(peoples,People::checkEvent);
        System.out.println("--------------------------");
        peoples.sort(People::comparePeople);
        Collections.sort(peoples,People::comparePeople);
    }
}
