package org.waldreg.home.homecontent.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.home.homecontent.dto.HomeContentDto;
import org.waldreg.home.spi.HomeRepository;

@Service
public class DefaultHomeManager implements HomeManager{

    private final HomeRepository homeRepository;

    @Autowired
    DefaultHomeManager(HomeRepository homeRepository){this.homeRepository = homeRepository;}

    @Override
    public void updateHome(HomeContentDto request){
        homeRepository.updateHome(request);
    }

    @Override
    public HomeContentDto getHome(){
        return homeRepository.getHome();
    }

}
