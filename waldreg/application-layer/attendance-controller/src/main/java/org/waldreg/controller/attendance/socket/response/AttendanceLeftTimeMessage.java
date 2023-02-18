package org.waldreg.controller.attendance.socket.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class AttendanceLeftTimeMessage{

    @JsonProperty("left_time")
    private Integer leftTime;

    public AttendanceLeftTimeMessage(){}

    public AttendanceLeftTimeMessage(int leftTime){
        this.leftTime = leftTime;
    }

    public Integer getLeftTime(){
        return leftTime;
    }

}
