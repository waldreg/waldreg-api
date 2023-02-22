package org.waldreg.controller.attendance.socket.request;

public final class AttendanceManagingMessage{

    private int id;

    public AttendanceManagingMessage(){}

    public AttendanceManagingMessage(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

}
