package org.waldreg.repository.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.domain.home.ApplicationColor;
import org.waldreg.home.color.dto.ColorDto;
import org.waldreg.home.spi.ColorRepository;
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
    public void updateColor(ColorDto request){
        ApplicationColor color = colorJpaRepository.findAll().get(0);
        color.setPrimaryColor(request.getPrimaryColor());
        color.setBackgroundColor(request.getBackgroundColor());
    }

    @Override
    @Transactional(readOnly = true)
    public ColorDto getColor(){
        ApplicationColor color = colorJpaRepository.findAll().get(0);
        return ColorDto.builder()
                .id(color.getId())
                .primaryColor(color.getPrimaryColor())
                .backgroundColor(color.getBackgroundColor())
                .build();
    }

}
