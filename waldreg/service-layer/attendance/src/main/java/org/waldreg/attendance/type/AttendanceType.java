package org.waldreg.attendance.type;

import java.util.Locale;
import org.waldreg.attendance.exception.UnknownAttendanceTypeException;

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

    public static AttendanceType getAttendanceType(String name){
        try{
            return AttendanceType.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException iae){
            throw new UnknownAttendanceTypeException(name);
        }
    }

}