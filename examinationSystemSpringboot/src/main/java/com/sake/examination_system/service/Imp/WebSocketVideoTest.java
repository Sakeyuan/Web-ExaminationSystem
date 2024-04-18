package com.sake.examination_system.service.Imp;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@ServerEndpoint("/video-stream/test")
public class WebSocketVideoTest {
    private static final Map<String, Session> sessionsMaps = new ConcurrentHashMap<>();
    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    @OnOpen
    public void onOpen(Session session) {

        sessions.add(session);
        System.out.println("建立连接，当前连接数: " + sessions.size());
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        sendAll(message, session);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        sessions.remove(session);
        session.close();
        System.out.println("关闭连接，当前连接数: " + sessions.size());
    }

    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
    }

    private void sendAll(String message, Session senderSession) {
        for (Session session : sessions) {
            if (!session.equals(senderSession)) {
                executorService.submit(() -> {
                    try {
                        if (session.isOpen()) {
                            session.getBasicRemote().sendText(message);
                        }
                    } catch (IOException e) {
                        System.err.println("Error sending message to client: " + e.getMessage());
                    }
                });
            }
        }
    }
}
