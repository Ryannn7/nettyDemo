package com.bufan.demo.service.netty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程池
 * @date 2022/3/2 下午7:50
 **/
public class SenderThreadPool {

    private static ExecutorService exe = Executors.newFixedThreadPool(10);

    public static void exe(Runnable run) {
        exe.execute(run);
    }
}
