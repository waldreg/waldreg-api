package org.waldreg.home.service.color.management;

import org.waldreg.home.service.color.dto.ColorDto;

public interface ColorManager{

    void updateColor(ColorDto request);

    ColorDto getColor();

}
