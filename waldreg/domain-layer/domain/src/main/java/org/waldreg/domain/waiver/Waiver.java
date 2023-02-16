package org.waldreg.domain.waiver;

import java.time.LocalDate;
import org.waldreg.domain.user.User;

public final class Waiver{

    private int waiverId;
    private final User waiverUser;
    private final LocalDate waiverDate;
    private final String waiverReason;

    private Waiver(Builder builder){
        this.waiverId = builder.waiverId;
        this.waiverUser = builder.waiverUser;
        this.waiverDate = builder.waiverDate;
        this.waiverReason = builder.waiverReason;
    }

    public static Builder builder(){
        return new Builder();
    }

    public void setWaiverId(int waiverId){
        this.waiverId = waiverId;
    }

    public int getWaiverId(){
        return waiverId;
    }

    public User getWaiverUser(){
        return waiverUser;
    }

    public LocalDate getWaiverDate(){
        return waiverDate;
    }

    public String getWaiverReason(){
        return waiverReason;
    }

    public static final class Builder{

        private int waiverId;
        private User waiverUser;
        private LocalDate waiverDate;
        private String waiverReason;

        private Builder(){}

        public Builder waiverId(int waiverId){
            this.waiverId = waiverId;
            return this;
        }

        public Builder waiverUser(User waiverUser){
            this.waiverUser = waiverUser;
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

        public Waiver build(){
            return new Waiver(this);
        }

    }

}
