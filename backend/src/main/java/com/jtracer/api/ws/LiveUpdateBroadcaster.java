package com.jtracer.api.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * Broadcasts live collector updates to connected WebSocket clients.
 */
@Service
public class LiveUpdateBroadcaster {

    private static final Logger log = LoggerFactory.getLogger(LiveUpdateBroadcaster.class);

    private final ObjectMapper objectMapper;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();

    public LiveUpdateBroadcaster(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void register(WebSocketSession session) {
        sessions.add(session);
    }

    public void unregister(WebSocketSession session) {
        sessions.remove(session);
    }

    public void broadcast(String event, Map<String, Object> payload) {
        if (sessions.isEmpty()) {
            return;
        }
        Map<String, Object> message =
                Map.of("event", event, "timestamp", Instant.now().toString(), "payload", payload);
        TextMessage textMessage;
        try {
            textMessage = new TextMessage(objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize live update for event {}: {}", event, e.getMessage());
            return;
        }
        for (WebSocketSession session : sessions) {
            if (!session.isOpen()) {
                sessions.remove(session);
                continue;
            }
            try {
                session.sendMessage(textMessage);
            } catch (IOException e) {
                log.debug("WebSocket send failed for session {}: {}", session.getId(), e.getMessage());
                sessions.remove(session);
            }
        }
    }
}
