package org.waldreg.controller.character.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class SpecifyStatusPermissionResponse{

    @JsonProperty("permission_name")
    private final String name;
    @JsonProperty("permission_info")
    private final String info;
    @JsonProperty("permission_status")
    private final String status;

    private SpecifyStatusPermissionResponse(){
        throw new UnsupportedOperationException("Can not invoke constructor \"SpecifyStatusPermissionResponse()\"");
    }

    private SpecifyStatusPermissionResponse(Builder builder){
        this.name = builder.name;
        this.info = builder.info;
        this.status = builder.status;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getName(){
        return this.name;
    }

    public String getInfo(){
        return this.info;
    }

    public String getStatus(){
        return status;
    }

    public static final class Builder{

        private String name;
        private String info;
        private String status;

        private Builder(){}

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder info(String info){
            this.info = info;
            return this;
        }

        public Builder status(String status){
            this.status = status;
            return this;
        }

        public SpecifyStatusPermissionResponse build(){
            return new SpecifyStatusPermissionResponse(this);
        }

    }

}