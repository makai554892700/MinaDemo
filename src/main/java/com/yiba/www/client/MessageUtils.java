package com.yiba.www.client;

import com.yiba.www.pojo.Message;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class MessageUtils {

    private static MessageUtils messageUtils = new MessageUtils();
    private SocketConnector connector;
    private IoSession session;
    private static final String IP_LOCATION = "127.0.0.1";
    private static final int PORT = 10086;
    private boolean isConnected;
    private boolean isResendding;
    private ArrayList<Message> messages = new ArrayList<>();

    private MessageUtils() {
        init();
    }

    public static MessageUtils getInstance() {
        return messageUtils;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (!isResendding) {
                reSend();
            }
            try {
                Thread.sleep(60000);
            } catch (Exception e) {
            }
            new Thread(runnable).start();
        }
    };

    private void init() {
        if (connector == null) {
            connector = new NioSocketConnector();
            DefaultIoFilterChainBuilder chain = connector.getFilterChain();
            chain.addLast("objDecode", new ProtocolCodecFilter(
                    new ObjectSerializationCodecFactory()));
            connector.setHandler(new IoHandlerAdapter() {
                @Override
                public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
                    isConnected = false;
                    super.exceptionCaught(session, cause);
                }
            });
            connect();
            new Thread(runnable).start();
        }
    }

    private void connect() {
        if (!isConnected) {
            ConnectFuture future = connector.connect(new InetSocketAddress(IP_LOCATION, PORT));
            future.awaitUninterruptibly();
            try {
                session = future.getSession();
            } catch (Exception e) {
                System.out.println("e=" + e);
                return;
            }
            session.getConfig().setUseReadOperation(true);
            isConnected = future.isConnected();
        }
    }

    private void reSend() {
        isResendding = true;
        int len = messages.size();
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                Message message = messages.remove(0);
                if (message != null) {
                    sendMessage(message.getKey(), message.getMessage());
                }
            }
        }
        isResendding = false;
    }

    public void sendMessage(String key, Object message) {
        if (key != null && !key.isEmpty() && message != null) {
            Message data = new Message(key, message);
            connect();
            if (isConnected) {
                session.write(data);
                System.out.println("message sended key=" + key + ";message=" + message);
            } else {
                if (messages.size() < 10000) {
                    messages.add(data);
                } else {
                    System.out.println("messages is big then 10000.");
                }
                System.out.println("connection lost.");
            }
        }
    }

    public void destroy() {
        CloseFuture future = session.getCloseFuture();
        future.awaitUninterruptibly(1000);
        connector.dispose();
    }

}
