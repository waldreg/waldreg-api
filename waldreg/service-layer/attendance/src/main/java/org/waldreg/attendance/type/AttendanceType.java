package org.waldreg.attendance.type;

import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.waldreg.attendance.exception.UnknownAttendanceTypeException;
import org.waldreg.attendance.type.spi.AttendanceTypeRepository;

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

    @Component
    static final class AttendanceTypeInitializer{

        private final AttendanceTypeRepository attendanceTypeRepository;

        @Autowired
        public AttendanceTypeInitializer(AttendanceTypeRepository attendanceTypeRepository){
            this.attendanceTypeRepository = attendanceTypeRepository;
        }

        @EventListener(ApplicationReadyEvent.class)
        void listenApplicationReady(){
            this.attendanceTypeRepository.createAttendanceTypeIfDoesNotExist(ATTENDANCED.toString());
            this.attendanceTypeRepository.createAttendanceTypeIfDoesNotExist(ACKNOWLEDGE_ABSENCE.toString());
            this.attendanceTypeRepository.createAttendanceTypeIfDoesNotExist(ABSENCE.toString());
            this.attendanceTypeRepository.createAttendanceTypeIfDoesNotExist(LATE_ATTENDANCE.toString());
        }

    }

}