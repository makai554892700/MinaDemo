package com.yiba.www.test;

import com.yiba.www.client.MessageUtils;

public class ClientTest {

    private static int temp = 0;

    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            MessageUtils.getInstance().sendMessage("test", "this is a test.");
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                System.out.println("e=" + e);
            }
            new Thread(runnable).start();
        }
    };

    public static void main(String args[]) {
//        new Thread(runnable).start();
        for (int i = 0; i < 50; i++) {
            MessageUtils.getInstance().sendMessage("test", "this is a test." + i);
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            }
        }
    }

}
