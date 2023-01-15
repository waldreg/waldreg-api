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
        private final Date createdAt;
        private final Date expiredAt;

        {
            createdAt = new Date();
            expiredAt = calculateExpiredAt();
        }

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public TokenDto build(){
            return new TokenDto(this);
        }

        private Date calculateExpiredAt(){
            Calendar calendar = Calendar.getInstance();
            int tokenExpirationMsec = 3600000;
            calendar.add(Calendar.MILLISECOND, tokenExpirationMsec);
            return calendar.getTime();
        }


    }

}
