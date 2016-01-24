package com.company.stream;

import com.company.lambda.People;

/**
 * Created by khangtnse60992 on 1/24/2016.
 */
public class Child {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Child converFromPeople(People p) {
        name = p.name;
        return this;
    }

    public Child() {
    }

    public Child(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Child{" +
                "name='" + name + '\'' +
                '}';
    }
}
