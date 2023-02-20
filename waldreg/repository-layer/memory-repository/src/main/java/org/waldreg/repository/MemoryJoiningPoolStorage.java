package org.waldreg.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;

@Repository
public class MemoryJoiningPoolStorage{

    private final MemoryUserStorage memoryUserStorage;
    private final AtomicInteger atomicInteger;
    private final Map<String, User> storage;
    private final int startIndex = 0;

    {
        storage = new HashMap<>();
        atomicInteger = new AtomicInteger(1);
    }


    public MemoryJoiningPoolStorage(MemoryUserStorage memoryUserStorage){this.memoryUserStorage = memoryUserStorage;}


    public void createUser(User user){
        user.setId(atomicInteger.getAndIncrement());
        storage.put(user.getUserId(),user);
    }

    public void deleteAllUser(){
        storage.clear();
    }

    public int readMaxIdx(){
        return storage.size();
    }

    public List<User> readJoiningPoolList(int startIdx,int endIdx){
        int cnt = startIndex;
        startIdx--;
        endIdx--;
        List<User> userList = new ArrayList<>();
        for (Map.Entry<String, User> userEntry : storage.entrySet()){
            if (cnt >= startIdx && cnt <= endIdx){
                userList.add(userEntry.getValue());
            }
            cnt++;
        }
        return userList;
    }

    public User getUserByUserId(String userId){
        for (Map.Entry<String, User> userEntry : storage.entrySet()){
            if (userEntry.getKey().equals(userId)){
                return userEntry.getValue();
            }
        }
        throw new IllegalStateException("Can not verifiable status of userId \"" + userId + "\"");
    }
    public void deleteUserByUserId(String userId){
        for (Map.Entry<String, User> userEntry : storage.entrySet()){
            if (userEntry.getKey().equals(userId)){
                storage.remove(userEntry.getKey(), userEntry.getValue());
                return;
            }
        }
        throw new IllegalStateException("Can not verifiable status of userId \"" + userId + "\"");
    }

    public boolean isExistUserId(String userId){
        for (Map.Entry<String, User> userEntry : storage.entrySet()){
            if (userEntry.getKey().equals(userId)){
                return true;
            }
        }
        return false;
    }


}
