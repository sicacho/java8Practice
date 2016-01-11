package com.company.lambda;

import java.util.List;

/**
 * Created by khangtnse60992 on 1/10/2016.
 */
public class PrintClass {
    interface Trigger {
        public boolean checkPeople(People p);
    }

    public static void printPeople(List<People> peoples,Trigger trigger) {
        for (int i = 0; i < peoples.size() ; i++) {
            if(trigger.checkPeople(peoples.get(i))) {
                System.out.println(peoples.get(i).name);
            }
        }
    }
}
