package org.waldreg.home.service.homecontent.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.home.core.HomeManager;
import org.waldreg.home.core.request.HomeRequestable;
import org.waldreg.home.core.response.HomeReadable;
import org.waldreg.home.core.spi.HomeRepository;

@Service
public class DefaultHomeManager implements HomeManager{

    private final HomeRepository homeRepository;

    @Autowired
    public DefaultHomeManager(HomeRepository homeRepository){this.homeRepository = homeRepository;}

    @Override
    public void updateHome(HomeRequestable request){
        homeRepository.updateHome(request);
    }

    @Override
    public HomeReadable getHome(){
        return homeRepository.getHome();
    }

}
