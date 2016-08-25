package com.company.interview.designpattern.decorator;

/**
 * Created by KhangTN1 on 8/22/2016.
 */
public class BorderDecorator extends Decorator{


    public BorderDecorator(LCD lcd) {
        super(lcd);
    }

    @Override
    public void draw() {
        super.draw();
        System.out.println("Draw with border");
    }
}
