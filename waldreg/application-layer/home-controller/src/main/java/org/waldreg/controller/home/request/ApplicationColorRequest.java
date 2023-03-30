package org.waldreg.controller.home.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.Pattern;

public final class ApplicationColorRequest{

    @JsonProperty("primary_color")
    @Pattern(regexp = "/#?([A-Fa-f0-9]){3}(([A-Fa-f0-9]){3})?/", message = "Invalid Color hex code \\”12345678910\\”")
    private String primaryColor;
    @JsonProperty("background_color")
    @Pattern(regexp = "/#?([A-Fa-f0-9]){3}(([A-Fa-f0-9]){3})?/", message = "Invalid Color hex code \\”12345678910\\”")
    private String backgroundColor;

    public ApplicationColorRequest(){}

    private ApplicationColorRequest(Builder builder){
        this.primaryColor = builder.primaryColor;
        this.backgroundColor = builder.backgroundColor;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getPrimaryColor(){
        return primaryColor;
    }

    public String getBackgroundColor(){
        return backgroundColor;
    }

    public void setPrimaryColor(String primaryColor){
        this.primaryColor = primaryColor;
    }

    public void setBackgroundColor(String backgroundColor){
        this.backgroundColor = backgroundColor;
    }

    public final static class Builder{

        private String primaryColor;
        private String backgroundColor;

        private Builder(){}

        public Builder primaryColor(String primaryColor){
            this.primaryColor = primaryColor;
            return this;
        }

        public Builder backgroundColor(String backgroundColor){
            this.backgroundColor = backgroundColor;
            return this;
        }

        public ApplicationColorRequest build(){
            return new ApplicationColorRequest(this);
        }

    }

}
