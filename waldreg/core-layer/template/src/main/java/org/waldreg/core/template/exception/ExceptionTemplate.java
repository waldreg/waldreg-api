package org.waldreg.core.template.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class ExceptionTemplate{

    private final String code;
    @JsonProperty("messages")
    private final String message;
    @JsonProperty("document_url")
    private final String documentUrl;

    private ExceptionTemplate(){
        throw new UnsupportedOperationException("Cannot invoke constructor \"ExceptionTemplate()\"");
    }

    private ExceptionTemplate(Builder builder){
        this.code = builder.code;
        this.message = builder.message;
        this.documentUrl = builder.documentUrl;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }

    public String getDocumentUrl(){
        return documentUrl;
    }

    public final static class Builder{


        private String code;
        private String message;

        private String documentUrl;

        private Builder(){}

        public Builder code(String code){
            this.code = code;
            return this;
        }

        public Builder message(String message){
            this.message = message;
            return this;
        }

        public Builder documentUrl(String documentUrl){
            this.documentUrl = documentUrl;
            return this;
        }

        public ExceptionTemplate build(){
            return new ExceptionTemplate(this);
        }

    }

}
