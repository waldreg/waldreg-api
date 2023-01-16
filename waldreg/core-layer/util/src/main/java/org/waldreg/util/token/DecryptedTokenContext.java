package org.waldreg.util.token;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.waldreg.util.exception.DecryptedTokenDoesNotExistException;

@Service
public class DecryptedTokenContext implements DecryptedTokenContextGetter, DecryptedTokenContextHolder, DecryptedTokenContextResolver{

    private static final ThreadLocal<Map<String, Integer>> threadLocal;
    private static final String key;

    static{
        threadLocal = ThreadLocal.withInitial(HashMap::new);
        key = "id";
    }

    public void hold(Integer id){
        threadLocal.get().put(key, id);
    }

    @Override
    public int get(){
        try{
            return threadLocal.get().get(key);
        } catch (NullPointerException NPE){
            throw new DecryptedTokenDoesNotExistException();
        }
    }

    public void resolve(){
        threadLocal.remove();
    }

}
