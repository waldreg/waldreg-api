package org.waldreg.home.spi;

import org.waldreg.home.homecontent.dto.HomeContentDto;

public interface HomeRepository{

    void updateHome(HomeContentDto request);

    HomeContentDto getHome();

}
