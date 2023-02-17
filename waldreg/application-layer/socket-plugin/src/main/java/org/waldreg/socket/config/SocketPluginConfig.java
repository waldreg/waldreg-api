package org.waldreg.socket.config;


import java.util.List;
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

    @Autowired
    public SocketPluginConfig(List<HandshakeInterceptor> handshakeInterceptorList,
                                    List<ChannelInterceptor> channelInterceptors){
        this.handshakeInterceptorList = handshakeInterceptorList;
        this.channelInterceptorList = channelInterceptors;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config){
        config.enableSimpleBroker("/topic");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/socket").withSockJS().setInterceptors(handshakeInterceptorList.toArray(HandshakeInterceptor[]::new));
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration){
        registration.interceptors(channelInterceptorList.toArray(ChannelInterceptor[]::new));
    }

}
