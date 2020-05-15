package cn.windf.study.lock;

import java.util.concurrent.*;

public class CountDownTest {
    public static void main(String[] args) {
        new CountDownTest().doBegin();
    }

    private CountDownLatch countDownLatch = new CountDownLatch(5);

    public void doBegin() {
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 0, TimeUnit.MICROSECONDS, new LinkedBlockingQueue());
        ExecutorService service = Executors.newFixedThreadPool(2);
        Runnable a = new A();

        for (int i = 0; i < 5; i++) {
            service.execute(a);
        }
        service.shutdown();

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            System.out.println("B is be Interrupted");
            e.printStackTrace();
        }

        System.out.println("B----");
    }

    class A implements Runnable {
        @Override
        public void run() {
            if (countDownLatch.getCount() > 0) {
                countDownLatch.countDown();
                System.out.println("A");
            }
        }
    }
}
