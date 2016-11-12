package com.company.interview.reflection.dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by Administrator on 8/28/2016.
 */
public class MyProxy implements InvocationHandler {

    private Object target;

    public MyProxy(Object target) {
        this.target = target;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        Method methodAnnotation = target.getClass().getMethod(method.getName(),method.getParameterTypes());
        if(method.getName().equals("parse") || methodAnnotation.isAnnotationPresent(FirstAnnotation.class)) {
            System.out.println("Before ");
            result = method.invoke(target,args);
            System.out.println("After ");
        } else {
            result = method.invoke(target,args);
        }
        return result;
    }
}
