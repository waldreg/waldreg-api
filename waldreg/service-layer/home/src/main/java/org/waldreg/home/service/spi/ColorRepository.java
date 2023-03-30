package org.waldreg.home.service.spi;

import org.waldreg.home.service.color.dto.ColorDto;

public interface ColorRepository{

    void updateColor(ColorDto request);

    ColorDto getColor();

}
