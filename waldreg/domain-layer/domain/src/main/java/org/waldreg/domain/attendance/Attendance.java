package org.waldreg.domain.attendance;

import java.time.LocalDate;
import java.util.Objects;

public final class Attendance{

    private int attendanceId;
    private final AttendanceUser attendanceUser;
    private AttendanceTypeReward attendanceType;
    private final LocalDate attendanceDate;

    private Attendance(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"Attendance()\"");
    }

    private Attendance(Builder builder){
        this.attendanceId = builder.attendanceId;
        this.attendanceUser = builder.attendanceUser;
        this.attendanceType = builder.attendanceType;
        this.attendanceDate = builder.attendanceDate;
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

    public LocalDate getAttendanceDate(){
        return attendanceDate;
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof Attendance)){
            return false;
        }
        Attendance attendance = (Attendance)object;
        return attendance.getAttendanceDate().equals(this.attendanceDate)
                && attendance.getAttendanceUser().getUser().getId() == this.attendanceUser.getUser().getId();
    }

    @Override
    public int hashCode(){
        return Objects.hash(this.attendanceDate, this.attendanceUser.getUser().getId());
    }

    public static final class Builder{

        private int attendanceId;
        private AttendanceUser attendanceUser;
        private AttendanceTypeReward attendanceType;
        private LocalDate attendanceDate;

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

        public Builder attendanceDate(LocalDate attendanceDate){
            this.attendanceDate = attendanceDate;
            return this;
        }

        public Attendance build(){
            return new Attendance(this);
        }

    }


}
