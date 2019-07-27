package com.example.piclient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ThreadManager
{

    private ExecutorService sockexec;//スレッドプール
    public ThreadManager(int maxthread){
        sockexec = Executors.newFixedThreadPool(maxthread);
    }
    public void submitThread(Control s){
        System.out.println("スレッド起動");
        sockexec.submit(s);
    }
    public void shutdownThreadPool(){
        sockexec.shutdown();
    }
}
