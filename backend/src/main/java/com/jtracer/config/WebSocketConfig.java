package com.jtracer.config;

import com.jtracer.api.ws.LiveWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final LiveWebSocketHandler liveWebSocketHandler;
    private final JtracerProperties properties;

    public WebSocketConfig(LiveWebSocketHandler liveWebSocketHandler, JtracerProperties properties) {
        this.liveWebSocketHandler = liveWebSocketHandler;
        this.properties = properties;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String[] origins = properties.getCors().getAllowedOrigins().toArray(String[]::new);
        registry.addHandler(liveWebSocketHandler, "/ws/live").setAllowedOrigins(origins);
    }
}
