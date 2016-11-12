package com.company.interview.reflection.dynamicProxy;

/**
 * Created by Administrator on 8/28/2016.
 */
public class ParserImpl implements Parser {
    @Override
    public void parse(String link) {
        System.out.println("Parse " + link);
    }

    @Override
    @FirstAnnotation
    public void documentLink(String link) {
        System.out.println("Document " + link);
    }
}
