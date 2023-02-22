package org.waldreg.controller.attendance.management.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.waldreg.attendance.type.AttendanceType;

public final class AttendanceCheckResponse{

    private final int id;

    @JsonProperty("user_id")
    private final String userId;

    @JsonProperty("attendance_required")
    private final boolean attendanceRequired;

    @JsonProperty("attendance_status")
    private final String attendanceStatus;

    private AttendanceCheckResponse(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"AttendanceCheckResponse()\"");
    }

    private AttendanceCheckResponse(Builder builder){
        this.id = builder.id;
        this.userId = builder.userId;
        this.attendanceRequired = builder.attendanceRequired;
        this.attendanceStatus = builder.attendanceStatus.toString();
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

    public String getAttendanceStatus(){
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

        public Builder attendanceRequired(boolean attendanceRequired){
            this.attendanceRequired = attendanceRequired;
            return this;
        }

        public Builder attendanceStatus(AttendanceType attendanceType){
            this.attendanceStatus = attendanceType;
            return this;
        }

        public AttendanceCheckResponse build(){
            return new AttendanceCheckResponse(this);
        }

    }

}
