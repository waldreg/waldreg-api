package org.waldreg.attendance.waiver.dto;

import java.time.LocalDate;

public final class WaiverDto{

    private final Integer waiverId;
    private final int id;
    private final String userId;
    private final String userName;
    private final LocalDate waiverDate;
    private final String waiverReason;

    private WaiverDto(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"WaiverDto()\"");
    }

    private WaiverDto(Builder builder){
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

    public Integer getWaiverId(){
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

        private Integer waiverId;
        private int id;
        private String userId;
        private String userName;
        private LocalDate waiverDate;
        private String waiverReason;

        private Builder(){}

        public Builder waiverId(Integer waiverId){
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

        public Builder waiverReason(String wavierReason){
            this.waiverReason = wavierReason;
            return this;
        }

        public WaiverDto build(){
            return new WaiverDto(this);
        }

    }

}
