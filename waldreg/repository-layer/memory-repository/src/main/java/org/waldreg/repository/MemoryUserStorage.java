package org.waldreg.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;
import org.waldreg.user.exception.UnknownIdException;
import org.waldreg.user.exception.UnknownUserIdException;

@Repository
public class MemoryUserStorage{

    private final MemoryCharacterStorage memoryCharacterStorage;
    private final AtomicInteger atomicInteger;
    private final Map<String, User> storage;

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

    private User setDefaultCharacter(User user){
        if (user.getName().equals("Admin")){
            user.setCharacter(memoryCharacterStorage.readCharacterByName("Admin"));
            return user;
        }
        user.setCharacter(memoryCharacterStorage.readCharacterByName("Guest"));
        return user;
    }

    public User readUserById(int id){
        System.out.println(id);
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
        int cnt = -1;
        startIdx -= 1;
        endIdx -= 1;
        List<User> userList = new ArrayList<>();
        for (Map.Entry<String, User> userEntry : storage.entrySet()){
            cnt++;
            if (cnt >= startIdx && cnt <= endIdx){
                userList.add(userEntry.getValue());
            }
        }
        return userList;
    }

    public int readMaxIdx(){
        return storage.size();
    }

    public void updateUser(String userId, User user){
        for(Map.Entry<String, User>userEntry : storage.entrySet()){
            if(userEntry.getValue().getUserId().equals(userId)){
                if(user.getName()!=null)userEntry.getValue().setName(user.getName());
                if(user.getUserPassword()!=null)userEntry.getValue().setUserPassword(user.getUserPassword());
                if(user.getPhoneNumber()!=null)userEntry.getValue().setPhoneNumber(user.getPhoneNumber());
            }
        }
    }

}
