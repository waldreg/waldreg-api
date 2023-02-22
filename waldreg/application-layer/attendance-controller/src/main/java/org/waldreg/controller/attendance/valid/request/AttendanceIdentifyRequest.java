package org.waldreg.controller.attendance.valid.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class AttendanceIdentifyRequest{

    @JsonProperty("attendance_identify")
    private String attendanceIdentify;

    public AttendanceIdentifyRequest(){}

    public String getAttendanceIdentify(){
        return attendanceIdentify;
    }

}
