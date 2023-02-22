package org.waldreg.controller.character.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public final class PermissionResponse{

    @JsonProperty("permission_id")
    private int id;
    @JsonProperty("permission_service")
    private String permissionService;
    @JsonProperty("permission_name")
    private String name;
    @JsonProperty("permission_info")
    private String info;
    @JsonProperty("permission_status")
    private List<String> statusList;

    public PermissionResponse(){}

    private PermissionResponse(Builder builder){
        this.id = builder.id;
        this.permissionService = builder.permissionService;
        this.name = builder.name;
        this.info = builder.info;
        this.statusList = builder.statusList;
    }

    public static Builder builder(){
        return new PermissionResponse.Builder();
    }

    public String getName(){
        return this.name;
    }

    public String getInfo(){
        return this.info;
    }

    public List<String> getStatusList(){
        return statusList;
    }

    public int getId(){
        return id;
    }

    public String getPermissionService(){
        return permissionService;
    }

    public static final class Builder{

        private int id;
        private String permissionService;
        private String name;
        private String info;
        private List<String> statusList;

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

        public Builder info(String info){
            this.info = info;
            return this;
        }

        public Builder statusList(List<String> statusList){
            this.statusList = statusList;
            return this;
        }

        public PermissionResponse build(){
            return new PermissionResponse(this);
        }

    }

}