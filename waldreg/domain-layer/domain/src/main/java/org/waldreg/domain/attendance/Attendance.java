package org.waldreg.domain.attendance;

public final class Attendance{

    private int attendanceId;
    private final AttendanceUser attendanceUser;
    private AttendanceTypeReward attendanceType;

    private Attendance(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"Attendance()\"");
    }

    private Attendance(Builder builder){
        this.attendanceId = builder.attendanceId;
        this.attendanceUser = builder.attendanceUser;
        this.attendanceType = builder.attendanceType;
    }

    public static Builder builder(){
        return new Builder();
    }

    public void setAttendanceId(int attendanceId){
        this.attendanceId = attendanceId;
    }

    public int getAttendanceId(){
        return attendanceId;
    }

    public AttendanceUser getAttendanceUser(){
        return attendanceUser;
    }

    public AttendanceTypeReward getAttendanceType(){
        return attendanceType;
    }

    public void setAttendanceType(AttendanceTypeReward attendanceType){
        this.attendanceType = attendanceType;
    }

    public static final class Builder{

        private int attendanceId;
        private AttendanceUser attendanceUser;
        private AttendanceTypeReward attendanceType;

        private Builder(){}

        public Builder attendanceId(int attendanceId){
            this.attendanceId = attendanceId;
            return this;
        }

        public Builder user(AttendanceUser attendanceUser){
            this.attendanceUser = attendanceUser;
            return this;
        }

        public Builder attendanceType(AttendanceTypeReward attendanceType){
            this.attendanceType = attendanceType;
            return this;
        }

        public Attendance build(){
            return new Attendance(this);
        }

    }


}
