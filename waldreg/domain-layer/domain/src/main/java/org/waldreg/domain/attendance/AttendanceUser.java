package org.waldreg.domain.attendance;

import org.waldreg.domain.user.User;

public final class AttendanceUser{

    private int attendanceUserId;
    private final User user;

    private AttendanceUser(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"AttendanceUser()\"");
    }

    private AttendanceUser(Builder builder){
        this.attendanceUserId = builder.attendanceUserId;
        this.user = builder.user;
    }

    public static Builder builder(){
        return new Builder();
    }

    public void setAttendanceUserId(int attendanceUserId){
        this.attendanceUserId = attendanceUserId;
    }

    public int getAttendanceUserId(){
        return attendanceUserId;
    }

    public User getUser(){
        return user;
    }

    public static final class Builder{

        private int attendanceUserId;
        private User user;

        private Builder(){}

        public Builder attendanceUserId(int attendanceUserId){
            this.attendanceUserId = attendanceUserId;
            return this;
        }

        public Builder user(User user){
            this.user = user;
            return this;
        }

        public AttendanceUser build(){
            return new AttendanceUser(this);
        }

    }

}
