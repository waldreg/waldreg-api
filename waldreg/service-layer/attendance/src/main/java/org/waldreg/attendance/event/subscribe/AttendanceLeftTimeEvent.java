package org.waldreg.attendance.event.subscribe;

public class AttendanceLeftTimeEvent{

    private int leftTime;

    public AttendanceLeftTimeEvent(int leftTime){
        this.leftTime = leftTime;
    }

    public void setLeftTime(int leftTime){
        this.leftTime = leftTime;
    }

    public int getLeftTime(){
        return leftTime;
    }

}
