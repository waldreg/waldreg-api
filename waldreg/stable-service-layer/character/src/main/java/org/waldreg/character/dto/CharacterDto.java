package org.waldreg.character.dto;

import java.util.List;

public final class CharacterDto{

    private final String characterName;
    private final List<PermissionDto> permissionDtoList;

    private CharacterDto(){
        throw new UnsupportedOperationException("Can not invoke constructor \"CharacterDto()\"");
    }

    private CharacterDto(Builder builder){
        this.characterName = builder.characterName;
        this.permissionDtoList = builder.permissionDtoList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getCharacterName(){
        return characterName;
    }

    public List<PermissionDto> getPermissionList(){
        return permissionDtoList;
    }

    public final static class Builder{

        private String characterName;
        private List<PermissionDto> permissionDtoList;

        private Builder(){}

        public Builder characterName(String characterName){
            this.characterName = characterName;
            return this;
        }

        public Builder permissionDtoList(List<PermissionDto> permissionDtoList){
            this.permissionDtoList = permissionDtoList;
            return this;
        }

        public CharacterDto build(){
            return new CharacterDto(this);
        }

    }

}
