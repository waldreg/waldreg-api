package org.waldreg.core.socket.sessionstage;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class SessionStage implements SessionStageGettable, SessionStageHoldable{

    private final ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap<>();

    @Override
    public int getIdAndDeprecate(String sessionId){
        int id = concurrentHashMap.get(sessionId);
        concurrentHashMap.remove(sessionId);
        return id;
    }

    @Override
    public void holdId(String sessionId, int id){
        concurrentHashMap.put(sessionId, id);
    }

}
