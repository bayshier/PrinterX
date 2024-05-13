package com.printer.tscdemo;


import java.util.concurrent.ThreadFactory;

/**
 * Created by Circle on 2018/5/24.
 */

/**
 * 作者： Circle
 * 创造于 2018/5/24.
 */
public class ThreadFactoryBuilder implements ThreadFactory {

    private String name;
    private int counter;

    public ThreadFactoryBuilder(String name) {
        this.name = name;
        counter = 1;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, name);
        thread.setName("ThreadFactoryBuilder_" + name + "_" + counter);
        return thread;
    }
}
