package org.waldreg.home.service.spi;

import org.waldreg.home.service.homecontent.dto.HomeContentDto;

public interface HomeRepository{

    void updateHome(HomeContentDto request);

    HomeContentDto getHome();

}
