package org.waldreg.attendance.management.dto;

import java.time.LocalDate;
import org.waldreg.attendance.type.AttendanceType;

public final class AttendanceStatusChangeDto{

    private final int id;
    private final AttendanceType attendanceType;
    private final LocalDate attendanceDate;

    private AttendanceStatusChangeDto(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"AttendanceStatusChangeDto()\"");
    }

    private AttendanceStatusChangeDto(Builder builder){
        this.id = builder.id;
        this.attendanceType = builder.attendanceType;
        this.attendanceDate = builder.attendanceDate;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public AttendanceType getAttendanceType(){
        return attendanceType;
    }

    public LocalDate getAttendanceDate(){
        return attendanceDate;
    }

    public static final class Builder{

        private int id;
        private AttendanceType attendanceType;
        private LocalDate attendanceDate;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder attendanceType(AttendanceType attendanceType){
            this.attendanceType = attendanceType;
            return this;
        }

        public Builder attendanceDate(LocalDate attendanceDate){
            this.attendanceDate = attendanceDate;
            return this;
        }

        public AttendanceStatusChangeDto build(){
            return new AttendanceStatusChangeDto(this);
        }

    }

}
