package org.waldreg.controller.attendance.socket.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class AttendanceIdentifyMessage{

    @JsonProperty("starter_id")
    private int starterId;

    @JsonProperty("attendance_identify")
    private String attendanceIdentify;

    public AttendanceIdentifyMessage(){}

    public AttendanceIdentifyMessage(int starterId, String attendanceIdentify){
        this.starterId = starterId;
        this.attendanceIdentify = attendanceIdentify;
    }

    public int getStarterId(){
        return starterId;
    }

    public String getAttendanceIdentify(){
        return attendanceIdentify;
    }

}
