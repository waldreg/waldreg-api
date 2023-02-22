package org.waldreg.core.socket.holder;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.waldreg.core.socket.sessionstage.SessionStageGettable;

@Component
@Scope(value = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SocketContextHolder implements SocketContenxtCascadable, SocketContextGettable{

    private int id;
    private boolean isCascaded = false;
    private final SessionStageGettable sessionStageGettable;
    private final Object lock = new Object();

    @Autowired
    public SocketContextHolder(SessionStageGettable sessionStageGettable){
        this.sessionStageGettable = sessionStageGettable;
    }

    @Override
    public void cascade(String sessionId){
        synchronized (lock){
            if (isCascaded){
                return;
            }
            id = sessionStageGettable.getIdAndDeprecate(sessionId);
            isCascaded = true;
        }
    }

    @Override
    public int getId(){
        return id;
    }

}
