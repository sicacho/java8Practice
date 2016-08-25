package com.company.interview.designpattern.proxy;

/**
 * Created by KhangTN1 on 8/24/2016.
 */
public class RealImage implements Image {

    private String filename;

    public RealImage(String filename) {
        this.filename = filename;
        loadFromDisk(filename);
    }

    private void loadFromDisk(String filename) {
        System.out.println("loading "+filename);
    }

    @Override
    public void displayImage() {
        System.out.println("Display "+filename);
    }
}
