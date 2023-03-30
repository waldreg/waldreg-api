package org.waldreg.home.service.logo;

import java.io.File;
import org.springframework.web.multipart.MultipartFile;

public interface LogoManager{

    void updateLogo(MultipartFile request);

    byte[] getLogo();

}
