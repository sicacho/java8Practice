package com.company.lambda;

import com.company.stream.Child;

import java.util.Optional;

/**
 * Created by khangtnse60992 on 1/10/2016.
 */
public class People {

    public People(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int id;
    public String name;

    public boolean checkEvent(){
        if(id%2==0)
            return true;
        return false;
    }

    public int comparePeople(People o2) {
        return o2.id - this.id;
    }

    public Optional<Child> convertToChild() {
        return Optional.of(new Child(name));
    }
}
