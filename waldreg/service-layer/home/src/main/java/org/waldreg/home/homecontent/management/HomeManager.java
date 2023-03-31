package org.waldreg.home.homecontent.management;

import org.waldreg.home.homecontent.dto.HomeContentDto;

public interface HomeManager{

    void updateHome(HomeContentDto request);

    HomeContentDto getHome();

}
