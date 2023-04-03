package org.waldreg.repository.home.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.waldreg.domain.home.ApplicationColor;
import org.waldreg.domain.home.HomeContent;
import org.waldreg.repository.home.repository.ColorJpaRepository;
import org.waldreg.repository.home.repository.HomeJpaRepository;

@Component
class HomeInitializer{

    @Autowired
    private ColorJpaRepository colorJpaRepository;

    @Autowired
    private HomeJpaRepository homeJpaRepository;

    @EventListener(ApplicationReadyEvent.class)
    void setUpApplicationColor(){
        if(colorJpaRepository.findAll().isEmpty()){
            colorJpaRepository.saveAndFlush(
                    ApplicationColor.builder()
                            .primaryColor("#2d2d2d")
                            .backgroundColor("#2d2d2d")
                            .build()
            );
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    void setUpHomeApplicationColor(){
        if(homeJpaRepository.findAll().isEmpty()){
            homeJpaRepository.saveAndFlush(
                    HomeContent.builder()
                            .content("Hello world   "
                                + "here is our github [waldreg](https://github.com/waldreg)   ")
                            .build()
            );
        }
    }

}
