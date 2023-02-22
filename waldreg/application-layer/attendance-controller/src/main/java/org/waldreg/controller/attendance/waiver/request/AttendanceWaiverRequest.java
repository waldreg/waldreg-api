package org.waldreg.controller.attendance.waiver.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public final class AttendanceWaiverRequest{

    @JsonProperty("waiver_date")
    private LocalDate waiverDate;
    @JsonProperty("waiver_reason")
    private String waiverReason;

    public AttendanceWaiverRequest(){}

    private AttendanceWaiverRequest(Builder builder){
        this.waiverDate = builder.waiverDate;
        this.waiverReason = builder.waiverReason;
    }

    public static Builder builder(){
        return new Builder();
    }

    public LocalDate getWaiverDate(){
        return waiverDate;
    }

    public String getWaiverReason(){
        return waiverReason;
    }

    public static final class Builder{

        private LocalDate waiverDate;
        private String waiverReason;

        private Builder(){}

        public Builder waiverDate(LocalDate waiverDate){
            this.waiverDate = waiverDate;
            return this;
        }

        public Builder waiverReason(String waiverReason){
            this.waiverReason = waiverReason;
            return this;
        }

        public AttendanceWaiverRequest build(){
            return new AttendanceWaiverRequest(this);
        }

    }

}