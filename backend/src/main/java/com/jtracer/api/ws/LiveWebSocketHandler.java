package com.jtracer.api.ws;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class LiveWebSocketHandler extends TextWebSocketHandler {

    private final LiveUpdateBroadcaster broadcaster;

    public LiveWebSocketHandler(LiveUpdateBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        broadcaster.register(session);
        try {
            session.sendMessage(new TextMessage(
                    "{\"event\":\"connected\",\"payload\":{\"endpoint\":\"/ws/live\"}}"));
        } catch (Exception ignored) {
            // Client may disconnect immediately; broadcaster handles cleanup on send.
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        broadcaster.unregister(session);
    }
}
