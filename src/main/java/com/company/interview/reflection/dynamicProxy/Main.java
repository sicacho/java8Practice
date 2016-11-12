package com.company.interview.reflection.dynamicProxy;

import java.lang.reflect.Proxy;

/**
 * Created by Administrator on 8/28/2016.
 */
public class Main  {


    public static void main(String[] args) {
        ParserImpl parser = new ParserImpl();
        Parser aop = (Parser) Proxy.newProxyInstance(Main.class.getClassLoader(),parser.getClass().getInterfaces(),new MyProxy(parser));
        aop.parse("abcs");
        aop.documentLink("abcs");

    }
}
