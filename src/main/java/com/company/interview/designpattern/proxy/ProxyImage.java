package com.company.interview.designpattern.proxy;

/**
 * Created by KhangTN1 on 8/24/2016.
 */
public class ProxyImage implements Image {

    Image image;
    String filename;

    public ProxyImage(String filename) {
        this.filename = filename;
    }

    @Override
    public void displayImage() {
        if(image==null) {
            image = new RealImage(filename);
        }
        image.displayImage();
    }
}
