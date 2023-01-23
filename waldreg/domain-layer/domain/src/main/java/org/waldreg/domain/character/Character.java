package org.waldreg.domain.character;

import java.util.List;

public final class Character{

    private final String characterName;
    private final List<Permission> permissionList;

    private Character(){
        throw new UnsupportedOperationException("Can not invoke constructor \"Character()\"");
    }

    private Character(Builder builder){
        this.characterName = builder.characterName;
        this.permissionList = builder.permissionList;
    }

    public static Builder builder(){
        return new Builder();
    }

    public String getCharacterName(){
        return characterName;
    }

    public List<Permission> getPermissionList(){
        return permissionList;
    }

    public final static class Builder{

        private String characterName;
        private List<Permission> permissionList;

        private Builder(){}

        public Builder characterName(String characterName){
            this.characterName = characterName;
            return this;
        }

        public Builder permissionList(List<Permission> permissionList){
            this.permissionList = permissionList;
            return this;
        }

        public Character build(){
            return new Character(this);
        }

    }

}
