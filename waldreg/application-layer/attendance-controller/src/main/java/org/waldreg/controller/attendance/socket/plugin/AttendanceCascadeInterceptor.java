package org.waldreg.controller.attendance.socket.plugin;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.waldreg.core.socket.holder.SocketContenxtCascadable;

@Component
public class AttendanceCascadeInterceptor implements ChannelInterceptor{

    private final SocketContenxtCascadable socketContenxtCascadable;

    public AttendanceCascadeInterceptor(SocketContenxtCascadable socketContenxtCascadable){
        this.socketContenxtCascadable = socketContenxtCascadable;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        socketContenxtCascadable.cascade(stompHeaderAccessor.getSessionId());
        return message;
    }

}
