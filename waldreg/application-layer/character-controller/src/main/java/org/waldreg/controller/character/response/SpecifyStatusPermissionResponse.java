package org.waldreg.controller.character.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class SpecifyStatusPermissionResponse{

    @JsonProperty("permission_id")
    private final int id;
    @JsonProperty("permission_service")
    private final String permissionService;
    @JsonProperty("permission_name")
    private final String name;
    @JsonProperty("permission_status")
    private final String status;

    private SpecifyStatusPermissionResponse(){
        throw new UnsupportedOperationException("Can not invoke constructor \"SpecifyStatusPermissionResponse()\"");
    }

    private SpecifyStatusPermissionResponse(Builder builder){
        this.id = builder.id;
        this.permissionService = builder.permissionService;
        this.name = builder.name;
        this.status = builder.status;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }


    public String getPermissionService(){
        return permissionService;
    }

    public String getName(){
        return this.name;
    }

    public String getStatus(){
        return status;
    }

    public static final class Builder{

        private int id;
        private String permissionService;
        private String name;
        private String status;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder permissionService(String permissionService){
            this.permissionService = permissionService;
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

        public SpecifyStatusPermissionResponse build(){
            return new SpecifyStatusPermissionResponse(this);
        }

    }

}