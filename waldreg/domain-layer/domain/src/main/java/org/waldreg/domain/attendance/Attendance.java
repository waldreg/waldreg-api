package org.waldreg.domain.attendance;

public final class Attendance{

    private final int attendanceId;
    private final AttendancePerDate attendancePerDate;
    private final AttendanceUser attendanceUser;
    private final AttendanceType attendanceType;

    private Attendance(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"Attendance()\"");
    }

    private Attendance(Builder builder){
        this.attendanceId = builder.attendanceId;
        this.attendancePerDate = builder.attendancePerDate;
        this.attendanceUser = builder.attendanceUser;
        this.attendanceType = builder.attendanceType;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getAttendanceId(){
        return attendanceId;
    }

    public AttendancePerDate getAttendancePerDate(){
        return attendancePerDate;
    }

    public AttendanceUser getAttendanceUser(){
        return attendanceUser;
    }

    public AttendanceType getAttendanceType(){
        return attendanceType;
    }

    public static final class Builder{

        private int attendanceId;
        private AttendancePerDate attendancePerDate;
        private AttendanceUser attendanceUser;
        private AttendanceType attendanceType;

        private Builder(){}

        public Builder attendanceId(int attendanceId){
            this.attendanceId = attendanceId;
            return this;
        }

        public Builder attendancePerDate(AttendancePerDate attendancePerDate){
            this.attendancePerDate = attendancePerDate;
            return this;
        }

        public Builder user(AttendanceUser attendanceUser){
            this.attendanceUser = attendanceUser;
            return this;
        }

        public Builder attendanceType(AttendanceType attendanceType){
            this.attendanceType = attendanceType;
            return this;
        }

        public Attendance build(){
            return new Attendance(this);
        }

    }


}
