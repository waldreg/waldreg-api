package org.waldreg.attendance.schedule.subscribe;

public final class AttendanceLeftTimeEvent{

    private final int leftTime;

    public AttendanceLeftTimeEvent(int leftTime){
        this.leftTime = leftTime;
    }

    public int getLeftTime(){
        return leftTime;
    }

}
