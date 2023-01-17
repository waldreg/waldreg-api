package org.waldreg.auth.exceptionadvice;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.waldreg.auth.request.AuthTokenRequest.Builder;

public class AuthExceptionTemplate{

    private String messages;

    @JsonProperty("document_url")
    private String documentUrl;

    private AuthExceptionTemplate(Builder builder){
        this.messages = builder.messages;
        this.documentUrl = builder.documentUrl;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getMessages(){
        return messages;
    }

    public String getDocumentUrl(){
        return documentUrl;
    }

    public static final class Builder{

        private String messages;
        private String documentUrl;

        private Builder(){}

        public Builder messages(String messages){
            this.messages = messages;
            return this;
        }

        public Builder documetUrl(String documentUrl){
            this.documentUrl = documentUrl;
            return this;
        }

        public AuthExceptionTemplate build(){
            return new AuthExceptionTemplate(this);
        }

    }

}
