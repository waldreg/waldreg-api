package org.waldreg.acceptance.attendance;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

public class AttendanceStompClient{

    private CustomStompSessionHandler stompSessionHandler;
    private final Set<String> attendanceNumbers = new HashSet<>();
    private String attendanceTime;

    private AttendanceStompClient(){}

    public static AttendanceStompClient getClient(){
        AttendanceStompClient client = new AttendanceStompClient();
        client.connect();
        return client;
    }

    private void connect(){
        List<Transport> transportList = List.of(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(new SockJsClient(transportList));
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        StompSessionHandler stompSessionHandler = new CustomStompSessionHandler();
        webSocketStompClient.connect("ws://127.0.0.1:9344/socket", stompSessionHandler);
    }

    public String getAttendanceNumber(){
        for(String number : attendanceNumbers){
            return number;
        }
        throw new IllegalStateException("Can not find any number");
    }

    public String getAttendanceTime(){
        return attendanceTime;
    }

    public void startAttendance(){
        stompSessionHandler.startAttendance();
    }

    public void stopAttendance(){
        stompSessionHandler.stopAttendance();
    }

    private final class CustomStompSessionHandler implements StompSessionHandler{

        private final Logger logger = Logger.getLogger("AttendanceStompClient");
        private StompSession session;

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders){
            subscribeAttendance(session);
            this.session = session;
        }

        private void subscribeAttendance(StompSession session){
            session.subscribe("/topic/attendance-key", this);
            logger.info("Subscribe /topic/attendance-key");
            session.subscribe("/topic/attendance-time", this);
            logger.info("Subscribe /topic/attendance-time");
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception){
            logger.info(() -> "error on stomp " + new String(payload));
            exception.printStackTrace();
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception){
            logger.info("error on connect socket");
            exception.printStackTrace();
        }

        @Override
        public Type getPayloadType(StompHeaders headers){
            return AttendanceMessage.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload){
            assert headers.getDestination() != null;
            if(headers.getDestination().contains("/topic/attendance-key")){
                attendanceNumbers.add(((AttendanceMessage) payload).getMessage());
            }
            if(headers.getDestination().contains("/topic/attendance-time")){
                attendanceTime = ((AttendanceMessage) payload).getMessage();
            }
        }

        public void startAttendance(){
            String attendanceStartDestination = "/attendance/start";
            session.send(attendanceStartDestination, new AttendanceMessage("start"));
        }

        public void stopAttendance(){
            String attendanceStopDestination = "/attendance/stop";
            session.send(attendanceStopDestination, new AttendanceMessage("stop"));
        }

    }

}
