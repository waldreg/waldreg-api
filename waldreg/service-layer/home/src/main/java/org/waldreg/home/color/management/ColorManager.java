package org.waldreg.home.color.management;

import org.waldreg.home.color.dto.ColorDto;

public interface ColorManager{

    void updateColor(ColorDto request);

    ColorDto getColor();

}
