package org.waldreg.attendance.waiver.dto;

import java.time.LocalDate;

public final class WaiverDto{

    private final int id;
    private final LocalDate waiverDate;
    private final String waiverReason;

    private WaiverDto(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"WaiverDto()\"");
    }

    private WaiverDto(Builder builder){
        this.id = builder.id;
        this.waiverDate = builder.waiverDate;
        this.waiverReason = builder.waiverReason;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public LocalDate getWaiverDate(){
        return waiverDate;
    }

    public String getWaiverReason(){
        return waiverReason;
    }

    public static final class Builder{

        private int id;
        private LocalDate waiverDate;
        private String waiverReason;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
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
