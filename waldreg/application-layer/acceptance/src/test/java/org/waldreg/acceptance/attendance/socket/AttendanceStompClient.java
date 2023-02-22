package org.waldreg.acceptance.attendance.socket;

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
import org.waldreg.controller.attendance.socket.request.AttendanceManagingMessage;
import org.waldreg.controller.attendance.socket.response.AttendanceIdentifyMessage;
import org.waldreg.controller.attendance.socket.response.AttendanceLeftTimeMessage;

public class AttendanceStompClient{

    private final Set<String> attendanceNumbers = new HashSet<>();
    private final int id;
    private Integer attendanceTime;
    private StompSession session;
    private final String token;
    private final Logger logger;

    private AttendanceStompClient(int id, String token){
        this.id = id;
        this.token = token;
        logger = Logger.getLogger("org.waldreg.acceptance.attendance.socket.AttendanceStompClient");
    }

    public synchronized static AttendanceStompClient getClient(int id, String token) throws Exception{
        AttendanceStompClient client = new AttendanceStompClient(id, token);
        client.connect();
        return client;
    }

    private synchronized void connect() throws Exception{
        List<Transport> transportList = List.of(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketStompClient webSocketStompClient = new WebSocketStompClient(new SockJsClient(transportList));
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        CustomStompSessionHandler stompSessionHandler = new CustomStompSessionHandler();
        this.session = webSocketStompClient.connect("ws://127.0.0.1:9344/socket?token=" + token, stompSessionHandler).get();
    }

    public synchronized boolean isUpdated(){
        return attendanceNumbers.size() > 0 && attendanceTime != null;
    }

    public synchronized String getAttendanceNumber(){
        for (String number : attendanceNumbers){
            return number;
        }
        throw new IllegalStateException("Can not find any number");
    }

    public synchronized int getAttendanceTime(){
        return attendanceTime;
    }

    public synchronized void startAttendance() throws Exception{
        String attendanceStartDestination = "/attendance/start";
        session.send(attendanceStartDestination, new AttendanceManagingMessage(id));
        logger.info(() -> "Send message start attendance id \"" + id + "\"");
        Thread.sleep(1000);
    }

    public synchronized void stopAttendance() throws Exception{
        String attendanceStopDestination = "/attendance/stop";
        session.send(attendanceStopDestination, new AttendanceManagingMessage(id));
        logger.info(() -> "Stop message stop attendance id \"" + id + "\"");
        Thread.sleep(1000);
    }

    private final class CustomStompSessionHandler implements StompSessionHandler{

        private final Logger logger = Logger.getLogger("AttendanceStompClient");

        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders){
            try{
                subscribeAttendance(session);
            } catch (Exception ignored){}
        }

        private void subscribeAttendance(StompSession session) throws Exception{
            session.subscribe("/topic/attendance-key", this);
            logger.info("Subscribe /topic/attendance-key");
            Thread.sleep(1000);
            session.subscribe("/topic/attendance-time", this);
            logger.info("Subscribe /topic/attendance-time");
            Thread.sleep(1000);
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
            if (headers.getDestination().contains("/topic/attendance-key")){
                return AttendanceIdentifyMessage.class;
            }
            if (headers.getDestination().contains("/topic/attendance-time")){
                return AttendanceLeftTimeMessage.class;
            }
            return AttendanceManagingMessage.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload){
            assert headers.getDestination() != null;
            if (headers.getDestination().contains("/topic/attendance-key")){
                logger.info(() -> "/topic/attendance-key reply \"" + ((AttendanceIdentifyMessage) payload).getAttendanceIdentify() + "\"");
                attendanceNumbers.add(((AttendanceIdentifyMessage) payload).getAttendanceIdentify());
            }
            if (headers.getDestination().contains("/topic/attendance-time")){
                logger.info(() -> "/topic/attendance-time reply \"" + ((AttendanceLeftTimeMessage) payload).getLeftTime() + "\"");
                attendanceTime = ((AttendanceLeftTimeMessage) payload).getLeftTime();
            }
        }

    }

}
