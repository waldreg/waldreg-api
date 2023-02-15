package org.waldreg.attendance.event.publish;

public final class AttendanceStartEvent{

    private final int attendanceStarterId;

    public AttendanceStartEvent(int attendanceStarterId){
        this.attendanceStarterId = attendanceStarterId;
    }

    public int getAttendanceStarterId(){
        return attendanceStarterId;
    }

}
