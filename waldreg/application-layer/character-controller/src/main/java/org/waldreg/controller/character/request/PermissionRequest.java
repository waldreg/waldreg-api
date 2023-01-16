package org.waldreg.controller.character.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;

public final class PermissionRequest{

    @NotBlank(message = "permission_name cannot be blank")
    @JsonProperty("permission_name")
    private String name;

    @NotBlank(message = "permission_status cannot be blank")
    @JsonProperty("permission_status")
    private String status;

    public PermissionRequest(){}

    private PermissionRequest(Builder builder){
        this.name = builder.name;
        this.status = builder.status;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public final static class Builder{

        private String name;
        private String status;

        private Builder(){}

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder status(String status){
            this.status = status;
            return this;
        }

        public PermissionRequest build(){
            return new PermissionRequest(this);
        }

    }

}