package org.waldreg.home.logo.management;

import org.springframework.web.multipart.MultipartFile;

public interface LogoManager{

    void updateLogo(MultipartFile request);

    byte[] getLogo();

}
