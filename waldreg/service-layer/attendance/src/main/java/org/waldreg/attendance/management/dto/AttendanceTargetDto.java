package org.waldreg.attendance.management.dto;

import org.waldreg.attendance.type.AttendanceType;

public final class AttendanceTargetDto{

    private final int id;
    private final String userId;
    private final boolean attendanceRequired;
    private final AttendanceType attendanceStatus;

    private AttendanceTargetDto(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"AttendanceTargetDto()\"");
    }

    private AttendanceTargetDto(Builder builder){
        this.id = builder.id;
        this.userId = builder.userId;
        this.attendanceRequired = builder.attendanceRequired;
        this.attendanceStatus = builder.attendanceStatus;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public String getUserId(){
        return userId;
    }

    public boolean isAttendanceRequired(){
        return attendanceRequired;
    }

    public AttendanceType getAttendanceStatus(){
        return attendanceStatus;
    }

    public static final class Builder{

        private int id;
        private String userId;
        private boolean attendanceRequired;
        private AttendanceType attendanceStatus;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder userId(String userId){
            this.userId = userId;
            return this;
        }

        public Builder attendanceStatus(AttendanceType attendanceType){
            this.attendanceRequired = attendanceType.isAttendanceRequire();
            this.attendanceStatus = attendanceType;
            return this;
        }

        public AttendanceTargetDto build(){
            return new AttendanceTargetDto(this);
        }

    }

}
