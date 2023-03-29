package org.waldreg.home.service.homecontent.management;

import org.waldreg.home.service.homecontent.dto.HomeContentDto;

public interface HomeManager{

    void updateHome(HomeContentDto request);

    HomeContentDto getHome();

}
