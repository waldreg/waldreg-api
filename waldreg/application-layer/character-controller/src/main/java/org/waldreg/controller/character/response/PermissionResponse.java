package org.waldreg.controller.character.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public final class PermissionResponse{

    @JsonProperty("permission_name")
    private final String name;
    @JsonProperty("permission_info")
    private final String info;
    @JsonProperty("permission_status")
    private final List<String> statusList;

    private PermissionResponse(){
        throw new UnsupportedOperationException("Can not invoke constructor \"PermissionResponse()\"");
    }

    private PermissionResponse(Builder builder){
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

    public static final class Builder{

        private String name;
        private String info;
        private List<String> statusList;

        private Builder(){}

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