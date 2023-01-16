package org.waldreg.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.character.exception.UnknownCharacterException;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.user.exception.UnknownIdException;
import org.waldreg.user.exception.UnknownUserIdException;

@Repository
public class MemoryUserStorage{

    private final MemoryCharacterStorage memoryCharacterStorage;
    private final AtomicInteger atomicInteger;
    private final Map<String, User> storage;
    private final int startIndex = 0;


    {
        storage = new HashMap<>();
        atomicInteger = new AtomicInteger(1);
    }

    @Autowired
    public MemoryUserStorage(MemoryCharacterStorage memoryCharacterStorage){this.memoryCharacterStorage = memoryCharacterStorage;}

    public void deleteAllUser(){storage.clear();}

    public void createUser(User user){
        setDefaultCharacter(user);
        user.setId(atomicInteger.getAndIncrement());
        storage.put(user.getName(), user);
    }

    private void setDefaultCharacter(User user){
        if (user.getName().equals("Admin")){
            throwIfUnknownCharacter("Admin");
            user.setCharacter(memoryCharacterStorage.readCharacterByName("Admin"));
            return;
        }
        throwIfUnknownCharacter("Guest");
        user.setCharacter(memoryCharacterStorage.readCharacterByName("Guest"));
    }

    public void updateCharacterOfUser(int id, String characterName){
        throwIfUnknownCharacter(characterName);
        Character character = memoryCharacterStorage.readCharacterByName(characterName);
        User user = readUserById(id);
        user.setCharacter(character);
        storage.put(user.getName(), user);
    }

    private void throwIfUnknownCharacter(String characterName){
        if (memoryCharacterStorage.readCharacterByName(characterName) == null){
            throw new UnknownCharacterException(characterName);
        }
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

    public List<User> readUserList(int startIdx, int endIdx){
        int cnt = startIndex;
        startIdx -= 1;
        endIdx -= 1;
        List<User> userList = new ArrayList<>();
        for (Map.Entry<String, User> userEntry : storage.entrySet()){
            if (cnt >= startIdx && cnt <= endIdx){
                userList.add(userEntry.getValue());
            }
            cnt++;
        }
        return userList;
    }

    public int readMaxIdx(){
        return storage.size();
    }

    public void updateUser(int id, User user){
        for (Map.Entry<String, User> userEntry : storage.entrySet()){
            if (userEntry.getValue().getId() == id){
                if (user.getName() != null){userEntry.getValue().setName(user.getName());}
                if (user.getUserPassword() != null){userEntry.getValue().setUserPassword(user.getUserPassword());}
                if (user.getPhoneNumber() != null){userEntry.getValue().setPhoneNumber(user.getPhoneNumber());}
            }
        }
    }

}
