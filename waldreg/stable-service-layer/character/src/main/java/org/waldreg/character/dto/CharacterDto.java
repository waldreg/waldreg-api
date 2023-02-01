package org.waldreg.character.dto;

import java.util.List;

public final class CharacterDto{

    private final int id;
    private final String characterName;
    private final List<PermissionDto> permissionDtoList;

    private CharacterDto(){
        throw new UnsupportedOperationException("Can not invoke constructor \"CharacterDto()\"");
    }

    private CharacterDto(Builder builder){
        this.id = builder.id;
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

    public int getId(){
        return id;
    }

    public final static class Builder{

        private int id;
        private String characterName;
        private List<PermissionDto> permissionDtoList;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

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
