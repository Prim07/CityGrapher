package com.agh.bsct.algorithm.services.runner;


import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.lang.NonNull;

import java.lang.reflect.Method;
import java.util.Arrays;

public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(@NonNull Throwable throwable,
                                        @NonNull Method method,
                                        @NonNull Object... objects) {
        System.out.println("Exception message - " + throwable.getMessage());
        System.out.println("Method name - " + method.getName());
        Arrays.stream(objects).forEach(o -> System.out.println("Param - " + o));
    }
}
