package com.open.coinnews.config;

import com.open.coinnews.ws.cmsnews.CmsNewsWebSocketHandler;
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
        registry.addHandler(cmsNewsWebSocketHandler(), "/newsws").setAllowedOrigins("*").withSockJS();
    }

    @Bean
    public WebSocketHandler cmsNewsWebSocketHandler() {
        return new PerConnectionWebSocketHandler(CmsNewsWebSocketHandler.class);
    }

}