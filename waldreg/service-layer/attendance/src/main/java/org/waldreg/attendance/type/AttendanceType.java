package org.waldreg.attendance.type;

public enum AttendanceType{

    ATTENDANCED("attendanced"),
    ACKNOWLEDGE_ABSENCE("acknowledge_absence"),
    ABSENCE("absence"),
    LATE_ATTENDANCE("late_attendance");

    private final String name;

    AttendanceType(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return name;
    }

}