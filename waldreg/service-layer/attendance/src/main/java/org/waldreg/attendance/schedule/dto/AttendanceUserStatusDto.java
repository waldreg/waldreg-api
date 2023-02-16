package org.waldreg.attendance.schedule.dto;

import org.waldreg.attendance.type.AttendanceType;

public final class AttendanceUserStatusDto{

    private final int id;
    private final AttendanceType attendanceType;

    private AttendanceUserStatusDto(Builder builder){
        this.id = builder.id;
        this.attendanceType = builder.attendanceType;
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

    public static final class Builder{

        private int id;
        private AttendanceType attendanceType;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder attendanceType(AttendanceType attendanceType){
            this.attendanceType = attendanceType;
            return this;
        }

        public AttendanceUserStatusDto build(){
            return new AttendanceUserStatusDto(this);
        }

    }

}
