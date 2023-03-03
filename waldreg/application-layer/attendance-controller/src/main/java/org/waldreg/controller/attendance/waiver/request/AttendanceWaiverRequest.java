package org.waldreg.controller.attendance.waiver.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.time.LocalDate;
import javax.validation.constraints.Size;

public final class AttendanceWaiverRequest{

    @JsonProperty("waiver_date")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate waiverDate;

    @Size(max = 1000, message = "ATTENDANCE-422 waiver_reason’s max size is 1000")
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