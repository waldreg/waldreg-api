package org.waldreg.controller.attendance.socket;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.waldreg.attendance.event.publish.AttendanceStartEvent;
import org.waldreg.attendance.event.publish.AttendanceStopEvent;
import org.waldreg.attendance.event.subscribe.AttendanceLeftTimeEvent;
import org.waldreg.attendance.event.subscribe.AttendanceStartedEvent;
import org.waldreg.character.aop.CharacterInUserReadable;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.exception.NoPermissionException;
import org.waldreg.character.permission.verification.PermissionVerifier;
import org.waldreg.controller.attendance.socket.request.AttendanceManagingMessage;
import org.waldreg.controller.attendance.socket.response.AttendanceIdentifyMessage;
import org.waldreg.controller.attendance.socket.response.AttendanceLeftTimeMessage;
import org.waldreg.core.socket.holder.SocketContextGettable;

@Controller
public class AttendanceSocketController{

    private final ApplicationEventPublisher applicationEventPublisher;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final CharacterInUserReadable characterInUserReadable;
    private final SocketContextGettable socketContextGettable;
    private final PermissionVerifier permissionVerifier;

    public AttendanceSocketController(ApplicationEventPublisher applicationEventPublisher,
                                        @Lazy SimpMessagingTemplate simpMessagingTemplate,
                                        CharacterInUserReadable characterInUserReadable,
                                        SocketContextGettable socketContextGettable,
                                        PermissionVerifier permissionVerifier){
        this.applicationEventPublisher = applicationEventPublisher;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.characterInUserReadable = characterInUserReadable;
        this.socketContextGettable = socketContextGettable;
        this.permissionVerifier = permissionVerifier;
    }

    @MessageMapping("/attendance/start")
    public void startAttendance(AttendanceManagingMessage attendanceManagingMessage, @Header("simpSessionId") String sessionId){
        permissionVerifying();
        AttendanceStartEvent attendanceStartEvent = new AttendanceStartEvent(socketContextGettable.getId(), sessionId);
        applicationEventPublisher.publishEvent(attendanceStartEvent);
    }

    @MessageMapping("/attendance/stop")
    public void stopAttendance(AttendanceManagingMessage attendanceManagingMessage){
        permissionVerifying();
        AttendanceStopEvent attendanceStopEvent = new AttendanceStopEvent(socketContextGettable.getId());
        applicationEventPublisher.publishEvent(attendanceStopEvent);
    }

    private void permissionVerifying(){
        int id = socketContextGettable.getId();
        CharacterDto characterDto = characterInUserReadable.readCharacterByUserId(id);
        characterDto.getPermissionList().stream().filter(c -> c.getName().equals("Attendance manager"))
                .findFirst()
                .ifPresentOrElse(c -> permissionVerifier.verify(c.getName(), c.getStatus())
                , NoPermissionException::new
                );
    }

    @EventListener(AttendanceLeftTimeEvent.class)
    public void listenAttendanceLeftTimeEvent(AttendanceLeftTimeEvent attendanceLeftTimeEvent){
        simpMessagingTemplate.convertAndSend("/topic/attendance-time",
                new AttendanceLeftTimeMessage(attendanceLeftTimeEvent.getLeftTime()));
    }

    @EventListener(AttendanceStartedEvent.class)
    public void listenAttendanceStartedEvent(AttendanceStartedEvent attendanceStartedEvent){
        simpMessagingTemplate.convertAndSend("/topic/attendance-key/" + attendanceStartedEvent.getAttendanceStartedSessionId(),
                new AttendanceIdentifyMessage(attendanceStartedEvent.getAttendanceStarterId(), attendanceStartedEvent.getAttendanceIdentify()));
    }

}
