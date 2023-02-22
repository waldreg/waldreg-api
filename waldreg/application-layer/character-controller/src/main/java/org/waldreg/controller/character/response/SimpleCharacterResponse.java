package org.waldreg.controller.character.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class SimpleCharacterResponse{

    private final int id;
    @JsonProperty("character_name")
    private final String characterName;

    private SimpleCharacterResponse(){
        throw new UnsupportedOperationException("Can not invoke constructor SimpleCharacterResponse()");
    }

    private SimpleCharacterResponse(Builder builder){
        this.id = builder.id;
        this.characterName = builder.characterName;
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

    public final static class Builder{
        private int id;
        private String characterName;

        private Builder(){}

        public Builder id(int id){
            this.id = id;
            return this;
        }

        public Builder characterName(String characterName){
            this.characterName = characterName;
            return this;
        }

        public SimpleCharacterResponse build(){
            return new SimpleCharacterResponse(this);
        }

    }

}
