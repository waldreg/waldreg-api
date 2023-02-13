package org.waldreg.acceptance.attendance;

public final class AttendanceMessage{

    private String message;

    public AttendanceMessage(){}

    public AttendanceMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

}
