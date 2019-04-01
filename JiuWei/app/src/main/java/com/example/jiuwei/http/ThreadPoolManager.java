package com.example.jiuwei.http;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



public class ThreadPoolManager {
    //1.把任务添加到请求队列中
    private LinkedBlockingDeque<Runnable> queue = new LinkedBlockingDeque<>();
        //添加任务
    public void execute(Runnable runnable){
        if(runnable!=null)
        try {
            queue.put(runnable);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //2.把队列中的任务放入线程池
        //用系统提供的线程池
    private ThreadPoolExecutor threadPoolExecutor;
        //构造方法中初始化
    private ThreadPoolManager(){
        // 1 最小容量  2 最大  3 4用来控制线程存活时间
        threadPoolExecutor = new ThreadPoolExecutor(4,20,15,
                TimeUnit.SECONDS,new ArrayBlockingQueue<Runnable>(4),
                rejectedExecutionHandler);
        //运行线程池
        threadPoolExecutor.execute(runnable);
    }
    //当线程数超过maxPoolSize或者keep-alive时间超时时执行拒绝策略
    private RejectedExecutionHandler
            rejectedExecutionHandler = new RejectedExecutionHandler(){
        /**
         * @param runnable 超时被线程池抛弃的线程
         * @param threadPoolExecutor
         */
        @Override
        public void rejectedExecution
        (Runnable runnable, ThreadPoolExecutor threadPoolExecutor) {
            //将该线程重新放入请求队列中
            try {
                queue.put(runnable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    };
    //3.让他们工作起来
    //整个的工作线程
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while(true){
                Runnable runnable = null ;
                //不断从从请求队列中取出请求
                try {
                    runnable = queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //如果不为空，放入线程池中执行
                if( runnable != null ){
                    threadPoolExecutor.execute(runnable);
                }
            }

        }
    };
    //单例模式
    private static ThreadPoolManager singleInstance = new ThreadPoolManager();
    public static ThreadPoolManager getOurInstance(){
        return singleInstance;
    }

}
