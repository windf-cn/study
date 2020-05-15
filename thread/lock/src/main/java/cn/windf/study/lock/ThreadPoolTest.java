package cn.windf.study.lock;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {
    private boolean timeToA = true;
    private final Object lock = new Object();
    private final Object lockB = new Object();

    public static void main(String[] args) {
//        new ThreadPoolTest().singlePool();
        new ThreadPoolTest().doublePool();
    }


    private void doublePool() {
        ThreadPoolExecutor executorA = new ThreadPoolExecutor(4, 4, 0, TimeUnit.MICROSECONDS, new LinkedBlockingQueue());
        ThreadPoolExecutor executorB = new ThreadPoolExecutor(4, 4, 0, TimeUnit.MICROSECONDS, new LinkedBlockingQueue());
        A a = new A();
        B b = new B();
        for (int i=0;i < 10000; i++) {
            executorA.execute(a);
            executorB.execute(b);
        }
    }

    private void singlePool() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 4, 0, TimeUnit.MICROSECONDS, new LinkedBlockingQueue());
        A a = new A();
        B b = new B();
        for (int i=0;i < 10000; i++) {
            executor.execute(a);
            executor.execute(a);
            executor.execute(b);
            executor.execute(b);
        }

        executor.shutdown();
    }

    private class A implements Runnable {

        @Override
        public void run() {
            synchronized (lock) {
                if (timeToA) {
                    System.out.println("a");
                    timeToA = false;
                }
            }
        }
    }

    private class B implements Runnable {
        @Override
        public void run() {
            synchronized (lockB) {
                if (!timeToA) {
                    System.out.println("b---");
                    timeToA = true;
                }
            }
        }
    }
}
