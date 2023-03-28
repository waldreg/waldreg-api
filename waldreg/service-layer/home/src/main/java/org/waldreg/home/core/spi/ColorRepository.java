package org.waldreg.home.core.spi;

import org.waldreg.home.core.request.ColorRequestable;
import org.waldreg.home.core.response.ColorReadable;

public interface ColorRepository{

    void updateColor(ColorRequestable request);

    ColorReadable getColor();

}
