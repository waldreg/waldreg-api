package org.waldreg.repository.home;

import org.springframework.stereotype.Repository;
import org.waldreg.home.core.request.HomeRequestable;
import org.waldreg.home.core.response.HomeReadable;
import org.waldreg.home.core.spi.HomeRepository;

@Repository
public class HomeRepositoryServiceProvider implements HomeRepository{

    @Override
    public void updateHome(HomeRequestable request){

    }

    @Override
    public HomeReadable getHome(){
        return null;
    }

}
