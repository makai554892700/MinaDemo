package com.yiba.www.test;

import com.yiba.www.server.MessageServer;

public class ServerTest {

    public static void main(String arg[]) {
        MessageServer.getInstance().regest("test", new MessageServer.Filter() {
            @Override
            public void onData(Object data) {
                System.out.println("data=" + data);
            }
        });
    }

}
