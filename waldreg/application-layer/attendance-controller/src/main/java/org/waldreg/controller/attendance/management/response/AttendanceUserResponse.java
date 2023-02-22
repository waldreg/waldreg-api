package org.waldreg.controller.attendance.management.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import org.waldreg.attendance.type.AttendanceType;

public final class AttendanceUserResponse{

    private final int id;

    @JsonProperty("user_id")
    private final String userId;

    @JsonProperty("user_name")
    private final String userName;

    @JsonProperty("attendances")
    private final List<AttendanceUserStatus> attendanceUserStatusList;

    private AttendanceUserResponse(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"AttendanceUserResponse()\"");
    }

    private AttendanceUserResponse(Builder builder){
        this.id = builder.id;
        this.userId = builder.userId;
        this.userName = builder.userName;
        this.attendanceUserStatusList = builder.attendanceUserStatusList;
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

    public String getUserName(){
        return userName;
    }

    public List<AttendanceUserStatus> getAttendanceUserStatusList(){
        return attendanceUserStatusList;
    }

    public static final class Builder{

        private int id;
        private String userId;
        private String userName;
        private List<AttendanceUserStatus> attendanceUserStatusList;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder userId(String userId){
            this.userId = userId;
            return this;
        }

        public Builder userName(String userName){
            this.userName = userName;
            return this;
        }

        public Builder attendanceUserStatusList(List<AttendanceUserStatus> attendanceUserStatusList){
            this.attendanceUserStatusList = attendanceUserStatusList;
            return this;
        }

        public AttendanceUserResponse build(){
            return new AttendanceUserResponse(this);
        }

    }

    public static final class AttendanceUserStatus{

        @JsonProperty("attendance_date")
        private final LocalDate attendanceDate;

        @JsonProperty("attendance_status")
        private final String attendanceStatus;

        private AttendanceUserStatus(){
            throw new UnsupportedOperationException("Cannot invoke constructor \"AttendanceUserStatus()\"");
        }

        private AttendanceUserStatus(AttendanceUserStatus.Builder builder){
            this.attendanceDate = builder.attendanceDate;
            this.attendanceStatus = builder.attendanceStatus.toString();
        }

        public static AttendanceUserStatus.Builder builder(){
            return new AttendanceUserStatus.Builder();
        }

        public LocalDate getAttendanceDate(){
            return attendanceDate;
        }

        public String getAttendanceStatus(){
            return attendanceStatus;
        }

        public static final class Builder{

            private LocalDate attendanceDate;
            private AttendanceType attendanceStatus;

            private Builder(){}

            public AttendanceUserStatus.Builder attendanceDate(LocalDate attendanceDate){
                this.attendanceDate = attendanceDate;
                return this;
            }

            public AttendanceUserStatus.Builder attendanceStatus(AttendanceType attendanceType){
                this.attendanceStatus = attendanceType;
                return this;
            }

            public AttendanceUserStatus build(){
                return new AttendanceUserStatus(this);
            }

        }

    }

}
