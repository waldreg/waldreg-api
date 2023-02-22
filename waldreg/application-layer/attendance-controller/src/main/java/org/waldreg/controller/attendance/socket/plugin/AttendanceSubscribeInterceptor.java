package org.waldreg.controller.attendance.socket.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class AttendanceSubscribeInterceptor implements ChannelInterceptor{

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public AttendanceSubscribeInterceptor(@Lazy SimpMessagingTemplate simpMessagingTemplate){
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel){
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        if(stompHeaderAccessor.getCommand() == StompCommand.SUBSCRIBE
            && ("/topic/attendance-key").equals(stompHeaderAccessor.getDestination())){
            String sessionId = stompHeaderAccessor.getSessionId();
            simpMessagingTemplate.send("/topic/attendance-key/" + sessionId, message);
            return null;
        }
        return message;
    }

}
