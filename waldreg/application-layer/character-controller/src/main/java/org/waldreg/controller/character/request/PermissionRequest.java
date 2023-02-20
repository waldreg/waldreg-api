package org.waldreg.controller.character.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public final class PermissionRequest{

    @NotNull(message = "CHARACTER-415 permission_id cannot be null")
    @JsonProperty("permission_id")
    private Integer id;

    @NotBlank(message = "CHARACTER-421 permission_service cannot be blank")
    @JsonProperty("permission_service")
    private String permissionService;

    @NotBlank(message = "CHARACTER-416 permission_name cannot be blank")
    @JsonProperty("permission_name")
    private String name;

    @NotBlank(message = "CHARACTER-417 permission_status cannot be blank")
    @JsonProperty("permission_status")
    private String status;

    public PermissionRequest(){}

    private PermissionRequest(Builder builder){
        this.id = builder.id;
        this.permissionService = builder.permissionService;
        this.name = builder.name;
        this.status = builder.status;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getName(){
        return name;
    }

    public String getStatus(){
        return status;
    }

    public Integer getId(){
        return id;
    }

    public String getPermissionService(){
        return permissionService;
    }

    public static final class Builder{

        private Integer id;
        private String permissionService;
        private String name;
        private String status;

        private Builder(){}

        public Builder id(Integer id){
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

        public PermissionRequest build(){
            return new PermissionRequest(this);
        }

    }

}