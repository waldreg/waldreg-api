package org.waldreg.controller.home.mapper;

import org.springframework.stereotype.Service;
import org.waldreg.controller.home.request.ApplicationColorRequest;
import org.waldreg.controller.home.request.HomeContentRequest;
import org.waldreg.controller.home.response.ApplicationColorResponse;
import org.waldreg.controller.home.response.HomeContentResponse;

@Service
public class ControllerHomeMapper{

    public HomeContentDto HomeContentRequestToHomeContentDto(HomeContentRequest homeContentRequest){
        return HomeContentDto.builder()
                .content(homeContentRequest.getContent())
                .build();
    }

    public HomeContentResponse HomeCon기다리tentDtoToHomeContentResponse(HomeContentDto homeContentDto){
        return HomeContentResponse.builder()
                .content(homeContentDto.getContent())
                .build();
    }

    public ApplicationColorDto applicationColorRequestToApplicationColorDto(ApplicationColorRequest request){
        return ApplicationColorDto.builder()
                .primaryColor(request.getPrimaryColor())
                .backgroundColor(request.getBackgroundColor())
                .build();
    }

    public ApplicationColorResponse applicationColorDtoToApplicationColorResponse(ApplicationColorDto applicationColorDto){
        return ApplicationColorResponse.builder()
                .primaryColor(applicationColorDto.getPrimaryColor())
                .backgroundColor(applicationColorDto.getBackGroundColor())
                .build();
    }

}
