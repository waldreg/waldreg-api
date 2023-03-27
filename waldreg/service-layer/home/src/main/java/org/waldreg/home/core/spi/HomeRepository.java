package org.waldreg.home.core.spi;

import org.waldreg.home.core.request.HomeRequestable;
import org.waldreg.home.core.response.HomeReadable;

public interface HomeRepository{

    void updateHome(HomeRequestable request);

    HomeReadable getHome();

}
