package org.waldreg.controller.attendance.socket.lock;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(scopeName = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Lock{

    public final Object lock;

    public Lock(){
        lock = new Object();
    }

}
