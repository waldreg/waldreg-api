package org.waldreg.attendance.type;

public enum AttendanceType{

    ATTENDANCED("attendanced", false),
    ACKNOWLEDGE_ABSENCE("acknowledge_absence", false),
    ABSENCE("absence", true),
    LATE_ATTENDANCE("late_attendance", false);

    private final String name;
    private final boolean attendanceRequire;

    AttendanceType(String name, boolean attendanceRequire){
        this.name = name;
        this.attendanceRequire = attendanceRequire;
    }

    @Override
    public String toString(){
        return name;
    }

    public boolean isAttendanceRequire(){
        return attendanceRequire;
    }

}