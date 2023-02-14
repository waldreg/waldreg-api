package org.waldreg.domain.attendance;

import java.time.LocalDate;
import java.util.List;

public final class AttendancePerDate{

    private final int attendancePerDateId;
    private final LocalDate attendanceDate;
    private final List<Attendance> attendanceList;

    private AttendancePerDate(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"AttendancePerDate()\"");
    }

    private AttendancePerDate(Builder builder){
        this.attendancePerDateId = builder.attendancePerDateId;
        this.attendanceDate = builder.attendanceDate;
        this.attendanceList = builder.attendanceList;
    }

    public int getAttendancePerDateId(){
        return attendancePerDateId;
    }

    public LocalDate getAttendanceDate(){
        return attendanceDate;
    }

    public List<Attendance> getAttendanceList(){
        return attendanceList;
    }

    public static final class Builder{

        private int attendancePerDateId;
        private LocalDate attendanceDate;
        private List<Attendance> attendanceList;

        private Builder(){}

        public Builder attendancePerDateId(int attendancePerDateId){
            this.attendancePerDateId = attendancePerDateId;
            return this;
        }

        public Builder attendanceDate(LocalDate attendanceDate){
            this.attendanceDate = attendanceDate;
            return this;
        }

        public Builder attendanceList(List<Attendance> attendanceList){
            this.attendanceList = attendanceList;
            return this;
        }

        public AttendancePerDate build(){
            return new AttendancePerDate(this);
        }

    }

}
