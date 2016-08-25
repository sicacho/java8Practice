package com.company.interview.designpattern.proxy;

/**
 * Created by KhangTN1 on 8/24/2016.
 * Proxy and Decorator both have the same interface as their wrapped types,
 * but the proxy creates an instance under the hood, whereas the decorator takes an instance in the constructor.
 */
public class Main {
    public static void main(String[] args) {
        Image image1 = new ProxyImage("C://image1.png");
        Image image2 = new ProxyImage("C://image2.png");
        image1.displayImage();
        image1.displayImage();
        image2.displayImage();
        image2.displayImage();
    }
}
