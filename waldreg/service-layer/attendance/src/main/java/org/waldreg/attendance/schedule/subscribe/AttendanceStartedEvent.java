package org.waldreg.attendance.schedule.subscribe;

public final class AttendanceStartedEvent{

    private final Integer attendanceStarterId;
    private final String attendanceIdentify;

    private AttendanceStartedEvent(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"AttendanceStartEvent()\"");
    }

    private AttendanceStartedEvent(Builder builder){
        this.attendanceStarterId = builder.attendanceStarterId;
        this.attendanceIdentify = builder.attendanceIdentify;
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

    public static final class Builder{

        private Integer attendanceStarterId;
        private String attendanceIdentify;

        private Builder(){}

        public Builder attendanceStarterId(Integer attendanceStarterId){
            this.attendanceStarterId = attendanceStarterId;
            return this;
        }

        public Builder attendanceIdentify(String attendanceIdentify){
            this.attendanceIdentify = attendanceIdentify;
            return this;
        }

        public AttendanceStartedEvent build(){
            return new AttendanceStartedEvent(this);
        }

    }

}
