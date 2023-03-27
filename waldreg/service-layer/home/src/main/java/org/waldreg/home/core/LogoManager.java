package org.waldreg.home.core;

import org.waldreg.home.core.request.LogoRequestable;
import org.waldreg.home.core.response.LogoReadable;

public interface LogoManager{

    void updateLogo(LogoRequestable request);

    LogoReadable getLogo();

}
