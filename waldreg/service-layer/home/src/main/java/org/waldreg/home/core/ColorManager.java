package org.waldreg.home.core;

import org.waldreg.home.core.request.ColorRequestable;
import org.waldreg.home.core.response.ColorReadable;

public interface ColorManager{

    void updateColor(ColorRequestable request);

    ColorReadable getColor();

}
