package com.company.interview.designpattern.decorator;

/**
 * Created by KhangTN1 on 8/22/2016.
 */
public class Main {
    public static void main(String[] args) {
        LCD lcd = new BorderDecorator(new Window());
        lcd.draw();
    }
}
