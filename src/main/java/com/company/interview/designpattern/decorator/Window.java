package com.company.interview.designpattern.decorator;

/**
 * Created by KhangTN1 on 8/22/2016.
 */
public class Window implements LCD {

    @Override
    public void draw() {
        System.out.println("Window simple");
    }
}
