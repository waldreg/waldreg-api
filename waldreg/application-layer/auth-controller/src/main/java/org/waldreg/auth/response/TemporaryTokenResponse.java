package org.waldreg.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TemporaryTokenResponse{

    @JsonProperty("temporary_token")
    private String temporaryToken;

    @JsonProperty("token_type")
    private String tokenType;

    public TemporaryTokenResponse(){}

    private TemporaryTokenResponse(Builder builder){
        this.temporaryToken = builder.temporaryToken;
        this.tokenType = builder.tokenType;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getTemporaryToken(){
        return temporaryToken;
    }

    public String getTokenType(){
        return tokenType;
    }

    public static final class Builder{

        private String temporaryToken;

        private String tokenType;

        private Builder(){}

        public Builder temporaryToken(String accessToken){
            this.temporaryToken = accessToken;
            return this;
        }

        public Builder tokenType(String tokenType){
            this.tokenType = tokenType;
            return this;
        }

        public TemporaryTokenResponse build(){
            return new TemporaryTokenResponse(this);
        }

    }

}
