package org.waldreg.config.character;

import org.springframework.context.ApplicationEvent;

public class GuestCharacterCreatedEvent extends ApplicationEvent{

    public GuestCharacterCreatedEvent(Object source){
        super(source);
    }

}
