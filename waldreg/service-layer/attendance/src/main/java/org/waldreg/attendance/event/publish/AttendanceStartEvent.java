package org.waldreg.attendance.event.publish;

public final class AttendanceStartEvent{

    private final int id;
    private final String sessionId;

    public AttendanceStartEvent(int id, String sessionId){
        this.id = id;
        this.sessionId = sessionId;
    }

    public int getId(){
        return id;
    }

    public String getSessionId(){
        return sessionId;
    }

}
