package com.vesense.demobank.yarn;

public class AppWorker {

    public static void main(String[] args) {
        Runnable r = new Runnable() {

            @Override
            public void run() {
                long count = 0;
                while (true) {
                    count++;
                    System.out.println("app worker : " + count);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };

        new Thread(r).start();

    }

}
