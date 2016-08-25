package com.company.interview.designpattern.decorator;

/**
 * Created by KhangTN1 on 8/22/2016.
 */
public class Decorator implements LCD{

    LCD lcd;

    public Decorator(LCD lcd) {
        this.lcd = lcd;
    }

    @Override
    public void draw() {
        lcd.draw();
    }
}
