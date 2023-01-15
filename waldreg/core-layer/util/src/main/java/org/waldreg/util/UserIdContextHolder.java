package org.waldreg.util;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class UserIdContextHolder{

    private static final ThreadLocal<Map<String, Integer>> threadLocal;
    private static final String key;

    static{
        threadLocal = ThreadLocal.withInitial(HashMap::new);
        key = "id";
    }

    public void hold(Integer id){
        threadLocal.get().put(key, id);
    }

    public int resolve(){
        int id = threadLocal.get().get(key);
        threadLocal.remove();
        return id;
    }

}
