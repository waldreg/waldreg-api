package org.waldreg.home.color.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.home.color.dto.ColorDto;
import org.waldreg.home.spi.ColorRepository;

@Service
public class DefaultColorManager implements ColorManager{

    private final ColorRepository colorRepository;

    @Autowired
    public DefaultColorManager(ColorRepository colorRepository){this.colorRepository = colorRepository;}

    @Override
    public void updateColor(ColorDto request){
        colorRepository.updateColor(request);
    }

    @Override
    public ColorDto getColor(){
        return colorRepository.getColor();
    }

}
