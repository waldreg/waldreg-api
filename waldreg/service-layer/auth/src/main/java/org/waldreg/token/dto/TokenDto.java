package org.waldreg.token.dto;

import java.util.Calendar;
import java.util.Date;

public class TokenDto{

    private int id;
    private final Date createdAt;
    private final Date expiredAt;

    private TokenDto(Builder builder){
        this.id = builder.id;
        this.createdAt = builder.createdAt;
        this.expiredAt = builder.expiredAt;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public Date getCreatedAt(){
        return createdAt;
    }

    public Date getExpiredAt(){
        return expiredAt;
    }

    public static final class Builder{

        private int id;
        private int expiredMilliSec;
        private Date createdAt;
        private Date expiredAt;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder expiredMilliSec(int expiredMilliSec){
            this.expiredMilliSec = expiredMilliSec;
            return this;
        }

        public TokenDto build(){
            initTokenValidatedDate();
            return new TokenDto(this);
        }

        private void initTokenValidatedDate(){
            createdAt = new Date();
            expiredAt = calculateExpiredAt();
        }

        private Date calculateExpiredAt(){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, expiredMilliSec);
            return calendar.getTime();
        }

    }

}
