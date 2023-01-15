package org.waldreg.repository;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.character.Character;

@Repository
public class MemoryCharacterStorage{

    private final Map<String, Character> storage;
    {
        storage = new HashMap<>();
    }

    public void createCharacter(Character character){
        this.storage.put(character.getCharacterName(), character);
    }

    public Character readCharacterByName(String name){
        return storage.get(name);
    }

    public void deleteAllCharacter(){
        storage.clear();
    }

    public Map<String, Character> readAllCharacter(){
        return storage;
    }

}
