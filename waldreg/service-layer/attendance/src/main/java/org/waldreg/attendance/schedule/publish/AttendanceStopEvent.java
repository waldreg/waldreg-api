package org.waldreg.attendance.schedule.publish;

public final class AttendanceStopEvent{

    private final int stopper;

    public AttendanceStopEvent(int stopper){
        this.stopper = stopper;
    }

    public int getStopper(){
        return stopper;
    }

}
