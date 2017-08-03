package com.yiba.www.server;

import com.yiba.www.pojo.Message;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class MessageServer {

    private static final int PORT = 10086;

    private static MessageServer messageServer = new MessageServer();

    private ConcurrentHashMap<String, Filter> filterMap = new ConcurrentHashMap<>();

    private MessageServer() {
        IoAcceptor ioAcceptor = new NioSocketAcceptor();
        ioAcceptor.getFilterChain().addLast("objDecode", new ProtocolCodecFilter(
                new ObjectSerializationCodecFactory()));
        ioAcceptor.setHandler(new IoHandlerAdapter() {
            @Override
            public void messageReceived(IoSession session, Object message) throws Exception {
                if (message instanceof Message) {
                    String key = ((Message) message).getKey();
                    Object value = ((Message) message).getMessage();
                    Filter filter = filterMap.get(key);
                    if (filter != null) {
                        filter.onData(value);
                    } else {
                        System.out.println("key not in filter");
                    }
                    System.out.println("key=" + key + ";value=" + value);
                } else {
                    System.out.println("message not instanceof Message.message=" + message);
                }
            }
        });
        ioAcceptor.getSessionConfig().setReadBufferSize(2048);
        ioAcceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        try {
            ioAcceptor.bind(new InetSocketAddress(PORT));
        } catch (IOException e) {
            System.out.println("e=" + e);
        }
    }

    public static MessageServer getInstance() {
        return messageServer;
    }

    public void regest(String key, Filter filter) {
        filterMap.put(key, filter);
    }

    public void unRegest(String key) {
        filterMap.remove(key);
    }

    public interface Filter {
        public void onData(Object data);
    }

}
