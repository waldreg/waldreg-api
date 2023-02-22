package org.waldreg.attendance.management.dto;

import java.time.LocalDate;
import java.util.List;
import org.waldreg.attendance.type.AttendanceType;

public final class AttendanceDayDto{

    private final LocalDate attendanceDate;
    private final List<AttendanceUserInDayDto> attendanceUserList;

    private AttendanceDayDto(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"AttendanceDayDto()\"");
    }

    private AttendanceDayDto(Builder builder){
        this.attendanceDate = builder.attendanceDate;
        this.attendanceUserList = builder.attendanceUserList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public LocalDate getAttendanceDate(){
        return attendanceDate;
    }

    public List<AttendanceUserInDayDto> getAttendanceUserList(){
        return attendanceUserList;
    }

    public static final class Builder{

        private LocalDate attendanceDate;
        private List<AttendanceUserInDayDto> attendanceUserList;

        private Builder(){}

        public Builder attendanceDate(LocalDate attendanceDate){
            this.attendanceDate = attendanceDate;
            return this;
        }

        public Builder attendanceUserList(List<AttendanceUserInDayDto> attendanceUserList){
            this.attendanceUserList = attendanceUserList;
            return this;
        }

        public AttendanceDayDto build(){
            return new AttendanceDayDto(this);
        }

    }

    public static final class AttendanceUserInDayDto{

        private final int id;
        private final String userId;
        private final String userName;
        private final AttendanceType attendanceStatus;

        private AttendanceUserInDayDto(){
            throw new UnsupportedOperationException("Cannot invoke constructor \"AttendanceUserInDayDto()\"");
        }

        private AttendanceUserInDayDto(AttendanceUserInDayDto.Builder builder){
            this.id = builder.id;
            this.userId = builder.userId;
            this.userName = builder.userName;
            this.attendanceStatus = builder.attendanceStatus;
        }

        public static AttendanceUserInDayDto.Builder builder(){
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

        public AttendanceType getAttendanceStatus(){
            return attendanceStatus;
        }

        public static final class Builder{

            private int id;
            private String userId;
            private String userName;
            private AttendanceType attendanceStatus;

            private Builder(){}

            public AttendanceUserInDayDto.Builder id(int id){
                this.id = id;
                return this;
            }

            public AttendanceUserInDayDto.Builder userId(String userId){
                this.userId = userId;
                return this;
            }

            public AttendanceUserInDayDto.Builder userName(String userName){
                this.userName = userName;
                return this;
            }

            public AttendanceUserInDayDto.Builder attendanceStatus(AttendanceType attendanceType){
                this.attendanceStatus = attendanceType;
                return this;
            }

            public AttendanceUserInDayDto build(){
                return new AttendanceUserInDayDto(this);
            }

        }

    }

}
