package org.waldreg.socket.config;


import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class SocketPluginConfig implements WebSocketMessageBrokerConfigurer{

    private final List<HandshakeInterceptor> handshakeInterceptorList;
    private final List<ChannelInterceptor> channelInterceptorList;
    private final Logger logger = Logger.getLogger("org.waldreg.socket.config.SocketPluginConfig");

    @Autowired
    public SocketPluginConfig(List<HandshakeInterceptor> handshakeInterceptorList,
                                    List<ChannelInterceptor> channelInterceptors){
        this.handshakeInterceptorList = handshakeInterceptorList;
        this.channelInterceptorList = channelInterceptors;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/topic");
        logger.info(() -> "Configure broker \"SimpleBroker\" to \"/topic\"");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/socket").withSockJS().setInterceptors(handshakeInterceptorList.toArray(HandshakeInterceptor[]::new));
        logger.info(() -> "Set end point to \"/socket\"");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(channelInterceptorList.toArray(ChannelInterceptor[]::new));
        logger.info(() -> "Configure client inbound channel");
    }

}
