package org.waldreg.controller.attendance.management.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import java.time.LocalDate;

public final class AttendanceModifyRequest{

    private int id;
    @JsonProperty("attendance_type")
    private String attendanceType;

    @JsonProperty("attendance_date")
    private LocalDate attendanceDate;

    public AttendanceModifyRequest(){}

    private AttendanceModifyRequest(Builder builder){
        this.id = builder.id;
        this.attendanceType = builder.attendanceType;
        this.attendanceDate = builder.attendanceDate;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public String getAttendanceType(){
        return attendanceType;
    }

    public LocalDate getAttendanceDate(){
        return attendanceDate;
    }

    public static final class Builder{

        private int id;
        private String attendanceType;
        private LocalDate attendanceDate;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder attendanceType(String attendanceType){
            this.attendanceType = attendanceType;
            return this;
        }

        public Builder attendanceDate(LocalDate attendanceDate){
            this.attendanceDate = attendanceDate;
            return this;
        }

        public AttendanceModifyRequest build(){
            return new AttendanceModifyRequest(this);
        }

    }

}