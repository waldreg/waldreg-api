package org.waldreg.controller.user.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;

public class CharacterRequest{
    @NotBlank(message = "Character name cannot be blank")
    @JsonProperty("character")
    private String character;

    public CharacterRequest(){}

    public String getCharacter(){
        return character;
    }

    public void setCharacter(String character){
        this.character = character;
    }

}
