package org.waldreg.controller.character.exceptionadvice;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class ExceptionTemplate{

    @JsonProperty("messages")
    private final String message;
    @JsonProperty("document_url")
    private final String documentUrl;

    private ExceptionTemplate(){
        throw new UnsupportedOperationException("Can not invoke constructor \"ExceptionTemplate()\"");
    }

    private ExceptionTemplate(Builder builder){
        this.message = builder.message;
        this.documentUrl = builder.documentUrl;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getMessage(){
        return message;
    }

    public String getDocumentUrl(){
        return documentUrl;
    }

    public final static class Builder{

        private String message;

        private String documentUrl;

        private Builder(){}

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
