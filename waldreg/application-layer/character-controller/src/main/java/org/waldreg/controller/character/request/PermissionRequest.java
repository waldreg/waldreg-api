package org.waldreg.controller.character.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public final class PermissionRequest{

    @NotNull(message = "permission_id cannot be null")
    @JsonProperty("permission_id")
    private int id;

    @NotBlank(message = "permission_name cannot be blank")
    @JsonProperty("permission_name")
    private String name;

    @NotBlank(message = "permission_status cannot be blank")
    @JsonProperty("permission_status")
    private String status;

    public PermissionRequest(){}

    private PermissionRequest(Builder builder){
        this.id = builder.id;
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

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public final static class Builder{

        private int id;
        private String name;
        private String status;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

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