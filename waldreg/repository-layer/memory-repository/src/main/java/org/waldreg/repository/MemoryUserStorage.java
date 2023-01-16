package org.waldreg.repository;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;
import org.waldreg.user.exception.UnknownIdException;

@Repository
public class MemoryUserStorage{

    private final Map<String, User> storage;

    {
        storage = new HashMap<>();
    }

    public void createUser(User user){
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

}
