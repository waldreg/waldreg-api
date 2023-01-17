package org.waldreg.config.character;

import org.springframework.context.ApplicationEvent;

public class AdminCharacterCreatedEvent extends ApplicationEvent{

    public AdminCharacterCreatedEvent(Object source){
        super(source);
    }

}
