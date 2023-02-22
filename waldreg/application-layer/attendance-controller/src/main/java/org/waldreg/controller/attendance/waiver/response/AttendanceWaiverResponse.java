package org.waldreg.controller.attendance.waiver.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public final class AttendanceWaiverResponse{

    @JsonProperty("waiver_id")
    private int waiverId;
    private int id;
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("user_name")
    private String userName;
    @JsonProperty("waiver_date")
    private LocalDate waiverDate;
    @JsonProperty("waiver_reason")
    private String waiverReason;

    public AttendanceWaiverResponse(){}

    private AttendanceWaiverResponse(Builder builder){
        this.waiverId = builder.waiverId;
        this.id = builder.id;
        this.userId = builder.userId;
        this.userName = builder.userName;
        this.waiverDate = builder.waiverDate;
        this.waiverReason = builder.waiverReason;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getWaiverId(){
        return waiverId;
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

    public LocalDate getWaiverDate(){
        return waiverDate;
    }

    public String getWaiverReason(){
        return waiverReason;
    }

    public static final class Builder{

        private int waiverId;
        private int id;
        private String userId;
        private String userName;
        private LocalDate waiverDate;
        private String waiverReason;

        private Builder(){}

        public Builder waiverId(int waiverId){
            this.waiverId = waiverId;
            return this;
        }

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

        public Builder waiverDate(LocalDate waiverDate){
            this.waiverDate = waiverDate;
            return this;
        }

        public Builder waiverReason(String waiverReason){
            this.waiverReason = waiverReason;
            return this;
        }

        public AttendanceWaiverResponse build(){
            return new AttendanceWaiverResponse(this);
        }

    }

}