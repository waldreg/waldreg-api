package org.waldreg.controller.character.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public final class CharacterResponse{

    private final int id;
    @JsonProperty("character_name")
    private final String characterName;
    @JsonProperty("permissions")
    private final List<SpecifyStatusPermissionResponse> permissionList;

    private CharacterResponse(){
        throw new UnsupportedOperationException("Can not invoke constructor \"CharacterResponse()\"");
    }

    private CharacterResponse(Builder builder){
        this.id = builder.id;
        this.characterName = builder.characterName;
        this.permissionList = builder.permissionList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public int getId(){
        return id;
    }

    public String getCharacterName(){
        return characterName;
    }

    public List<SpecifyStatusPermissionResponse> getPermissionList(){
        return permissionList;
    }

    public final static class Builder{

        private int id;
        private String characterName;
        private List<SpecifyStatusPermissionResponse> permissionList;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder characterName(String characterName){
            this.characterName = characterName;
            return this;
        }

        public Builder permissionList(List<SpecifyStatusPermissionResponse> permissionList){
            this.permissionList = permissionList;
            return this;
        }

        public CharacterResponse build(){
            return new CharacterResponse(this);
        }

    }

}
