package org.waldreg.repository.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.home.core.request.ColorRequestable;
import org.waldreg.home.core.response.ColorReadable;
import org.waldreg.home.core.spi.ColorRepository;
import org.waldreg.repository.home.repository.ColorJpaRepository;

@Repository
public class ColorRepositoryServiceProvider implements ColorRepository{

    private final ColorJpaRepository colorJpaRepository;

    @Autowired
    ColorRepositoryServiceProvider(ColorJpaRepository colorJpaRepository){
        this.colorJpaRepository = colorJpaRepository;
    }

    @Override
    @Transactional
    public void updateColor(ColorRequestable request){

    }

    @Override
    @Transactional(readOnly = true)
    public ColorReadable getColor(){

    }

}
