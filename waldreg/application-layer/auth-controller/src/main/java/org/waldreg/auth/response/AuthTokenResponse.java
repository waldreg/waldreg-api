package org.waldreg.auth.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthTokenResponse{

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    public AuthTokenResponse(){}

    private AuthTokenResponse(Builder builder){
        this.accessToken = builder.accessToken;
        this.tokenType = builder.tokenType;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getAccessToken(){
        return accessToken;
    }

    public String getTokenType(){
        return tokenType;
    }

    public static final class Builder{

        private String accessToken;

        private String tokenType;

        private Builder(){}

        public Builder accessToken(String accessToken){
            this.accessToken = accessToken;
            return this;
        }

        public Builder tokenType(String tokenType){
            this.tokenType = tokenType;
            return this;
        }

        public AuthTokenResponse build(){
            return new AuthTokenResponse(this);
        }

    }

}
