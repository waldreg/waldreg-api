package org.waldreg.repository;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;
import org.waldreg.user.exception.UnknownIdException;
import org.waldreg.user.exception.UnknownUserIdException;

@Repository
public class MemoryUserStorage{

    private final MemoryCharacterStorage memoryCharacterStorage;

    private final Map<String, User> storage;

    {
        storage = new HashMap<>();
    }

    @Autowired
    public MemoryUserStorage(MemoryCharacterStorage memoryCharacterStorage){this.memoryCharacterStorage = memoryCharacterStorage;}

    public void deleteAllUser(){storage.clear();}

    public void createUser(User user){
        if (user.getCharacter() == null){
            user.setCharacter(memoryCharacterStorage.readCharacterByName("Guest"));
        }
        storage.put(user.getName(), user);
    }

    public User readUserById(int id){
        for (Map.Entry<String, User> userEntry : storage.entrySet()){
            if (userEntry.getValue().getId() == id){
                return userEntry.getValue();
            }
        }
        throw new UnknownIdException(id);
    }

    public User readUserByUserId(String userId){
        for (Map.Entry<String, User> userEntry : storage.entrySet()){
            if (userEntry.getValue().getUserId().equals(userId)){
                return userEntry.getValue();
            }
        }
        throw new UnknownUserIdException(userId);
    }

}
