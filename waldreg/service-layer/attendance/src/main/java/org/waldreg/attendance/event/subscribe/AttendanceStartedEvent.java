package org.waldreg.attendance.event.subscribe;

public final class AttendanceStartedEvent{

    private final int attendanceStarterId;
    private final String attendanceIdentify;
    private final String attendanceStartedSessionId;

    private AttendanceStartedEvent(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"AttendanceStartEvent()\"");
    }

    private AttendanceStartedEvent(Builder builder){
        this.attendanceStarterId = builder.attendanceStarterId;
        this.attendanceIdentify = builder.attendanceIdentify;
        this.attendanceStartedSessionId = builder.attendanceStarterSessionId;
    }

    public static Builder builder(){
        return new Builder();
    }

    public Integer getAttendanceStarterId(){
        return attendanceStarterId;
    }

    public String getAttendanceIdentify(){
        return attendanceIdentify;
    }

    public String getAttendanceStartedSessionId(){
        return attendanceStartedSessionId;
    }

    public static final class Builder{

        private Integer attendanceStarterId;
        private String attendanceIdentify;
        private String attendanceStarterSessionId;

        private Builder(){}

        public Builder attendanceStarterId(Integer attendanceStarterId){
            this.attendanceStarterId = attendanceStarterId;
            return this;
        }

        public Builder attendanceIdentify(String attendanceIdentify){
            this.attendanceIdentify = attendanceIdentify;
            return this;
        }

        public Builder attendanceStarterSessionId(String attendanceStarterSessionId){
            this.attendanceStarterSessionId = attendanceStarterSessionId;
            return this;
        }

        public AttendanceStartedEvent build(){
            return new AttendanceStartedEvent(this);
        }

    }

}
