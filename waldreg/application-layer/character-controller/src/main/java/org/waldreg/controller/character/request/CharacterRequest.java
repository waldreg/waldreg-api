package org.waldreg.controller.character.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public final class CharacterRequest{

    @NotBlank(message = "CHARACTER-413 character_name cannot be blank")
    @JsonProperty("character_name")
    private String characterName;

    @NotNull(message = "CHARACTER-414 permissions cannot be null")
    @JsonProperty("permissions")
    private List<PermissionRequest> permissionList;

    public CharacterRequest(){}

    private CharacterRequest(Builder builder){
        this.characterName = builder.characterName;
        this.permissionList = builder.permissionList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getCharacterName(){
        return characterName;
    }

    public void setCharacterName(String characterName){
        this.characterName = characterName;
    }

    public List<PermissionRequest> getPermissionList(){
        return permissionList;
    }

    public void setPermissionList(List<PermissionRequest> permissionList){
        this.permissionList = permissionList;
    }

    public final static class Builder{

        private String characterName;
        private List<PermissionRequest> permissionList;

        private Builder(){}

        public Builder characterName(String characterName){
            this.characterName = characterName;
            return this;
        }

        public Builder permissionList(List<PermissionRequest> permissionList){
            this.permissionList = permissionList;
            return this;
        }

        public CharacterRequest build(){
            return new CharacterRequest(this);
        }

    }

}