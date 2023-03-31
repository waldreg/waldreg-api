package org.waldreg.repository.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.domain.home.HomeContent;
import org.waldreg.home.spi.HomeRepository;
import org.waldreg.home.homecontent.dto.HomeContentDto;
import org.waldreg.repository.home.repository.HomeJpaRepository;

@Repository
public class HomeRepositoryServiceProvider implements HomeRepository{

    private final HomeJpaRepository homeJpaRepository;

    @Autowired
    HomeRepositoryServiceProvider(HomeJpaRepository homeJpaRepository){
        this.homeJpaRepository = homeJpaRepository;
    }

    @Override
    @Transactional
    public void updateHome(HomeContentDto request){
        HomeContent homeContent = homeJpaRepository.findAll().get(0);
        homeContent.setContent(request.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public HomeContentDto getHome(){
        HomeContent homeContent = homeJpaRepository.findAll().get(0);
        return HomeContentDto.builder()
                .id(homeContent.getId())
                .content(homeContent.getContent())
                .build();
    }

}
