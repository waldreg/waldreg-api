package org.waldreg.home.spi;

import org.waldreg.home.color.dto.ColorDto;

public interface ColorRepository{

    void updateColor(ColorDto request);

    ColorDto getColor();

}
