package org.waldreg.controller.attendance.management.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import org.waldreg.attendance.type.AttendanceType;

public final class AttendancePerDayResponse{

    @JsonProperty("attendance_date")
    private final LocalDate attendanceDate;

    @JsonProperty("attendance_users")
    private final List<AttendanceUserInDayResponse> attendanceUserList;

    private AttendancePerDayResponse(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"AttendancePerDayResponse()\"");
    }

    private AttendancePerDayResponse(Builder builder){
        this.attendanceDate = builder.attendanceDate;
        this.attendanceUserList = builder.attendanceUserList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public LocalDate getAttendanceDate(){
        return attendanceDate;
    }

    public List<AttendanceUserInDayResponse> getAttendanceUserList(){
        return attendanceUserList;
    }

    public static final class Builder{

        private LocalDate attendanceDate;
        private List<AttendanceUserInDayResponse> attendanceUserList;

        private Builder(){}

        public Builder attendanceDate(LocalDate attendanceDate){
            this.attendanceDate = attendanceDate;
            return this;
        }

        public Builder attendanceUserList(List<AttendanceUserInDayResponse> attendanceUserList){
            this.attendanceUserList = attendanceUserList;
            return this;
        }

        public AttendancePerDayResponse build(){
            return new AttendancePerDayResponse(this);
        }

    }

    public static final class AttendanceUserInDayResponse{

        private final int id;

        @JsonProperty("user_id")
        private final String userId;

        @JsonProperty("user_name")
        private final String userName;

        @JsonProperty("attendance_status")
        private final AttendanceType attendanceStatus;

        private AttendanceUserInDayResponse(){
            throw new UnsupportedOperationException("Cannot invoke constructor \"AttendanceUserInDayDto()\"");
        }

        private AttendanceUserInDayResponse(Builder builder){
            this.id = builder.id;
            this.userId = builder.userId;
            this.userName = builder.userName;
            this.attendanceStatus = builder.attendanceStatus;
        }

        public static AttendanceUserInDayResponse.Builder builder(){
            return new AttendanceUserInDayResponse.Builder();
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

        public AttendanceType getAttendanceStatus(){
            return attendanceStatus;
        }

        public static final class Builder{

            private int id;
            private String userId;
            private String userName;
            private AttendanceType attendanceStatus;

            private Builder(){}

            public AttendanceUserInDayResponse.Builder id(int id){
                this.id = id;
                return this;
            }

            public AttendanceUserInDayResponse.Builder userId(String userId){
                this.userId = userId;
                return this;
            }

            public AttendanceUserInDayResponse.Builder userName(String userName){
                this.userName = userName;
                return this;
            }

            public AttendanceUserInDayResponse.Builder attendanceStatus(AttendanceType attendanceType){
                this.attendanceStatus = attendanceType;
                return this;
            }

            public AttendanceUserInDayResponse build(){
                return new AttendanceUserInDayResponse(this);
            }

        }

    }

}
