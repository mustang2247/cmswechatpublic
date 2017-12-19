package com.open.coinnews.config;

import com.open.coinnews.ws.snake.SnakeWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

@Configuration
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(snakeWebSocketHandler(), "/snake").withSockJS();
    }

    /**
     * 贪吃蛇
     * @return
     */
    @Bean
    public WebSocketHandler snakeWebSocketHandler() {
        return new PerConnectionWebSocketHandler(SnakeWebSocketHandler.class);
    }
}