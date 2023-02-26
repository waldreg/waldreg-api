package org.waldreg.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.character.Character;

@Repository
public class MemoryCharacterStorage{

    private final Map<String, Character> storage;
    private final AtomicInteger atomicInteger;

    {
        storage = new HashMap<>();
        atomicInteger = new AtomicInteger(1);
    }

    public void createCharacter(Character character){
        return;
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

    public void deleteCharacterByName(String name){
        storage.remove(name);
    }

    public boolean isExistCharacterName(String name){
        if(storage.containsKey(name)){
            return true;
        }
        return false;
    }

}
