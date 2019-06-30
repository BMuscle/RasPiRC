package com.example.piclient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ThreadManager
{

    private ExecutorService sockexec;
    public ThreadManager(int maxthread){
        sockexec = Executors.newFixedThreadPool(maxthread);
    }
    public void Submit_Thread(SocketClient s){
        System.out.println("スレッド起動");
        sockexec.submit(s);
    }
}