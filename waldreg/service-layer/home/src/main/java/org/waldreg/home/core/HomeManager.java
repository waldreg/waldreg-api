package org.waldreg.home.core;

import org.waldreg.home.core.request.HomeRequestable;
import org.waldreg.home.core.response.HomeReadable;

public interface HomeManager{

    void updateHome(HomeRequestable request);

    HomeReadable getHome();

}
