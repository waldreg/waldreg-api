package org.waldreg.controller.home.mapper;

import org.springframework.stereotype.Service;
import org.waldreg.controller.home.request.ApplicationColorRequest;
import org.waldreg.controller.home.request.HomeContentRequest;
import org.waldreg.controller.home.response.ApplicationColorResponse;
import org.waldreg.controller.home.response.HomeContentResponse;
import org.waldreg.home.service.color.dto.ColorDto;
import org.waldreg.home.service.homecontent.dto.HomeContentDto;

@Service
public class ControllerHomeMapper{

    public HomeContentDto HomeContentRequestToHomeContentDto(HomeContentRequest homeContentRequest){
        return HomeContentDto.builder()
                .content(homeContentRequest.getContent())
                .build();
    }

    public HomeContentResponse HomeContentDtoToHomeContentResponse(HomeContentDto homeContentDto){
        return HomeContentResponse.builder()
                .content(homeContentDto.getContent())
                .build();
    }

    public ColorDto colorRequestToApplicationColorDto(ApplicationColorRequest request){
        return ColorDto.builder()
                .primaryColor(request.getPrimaryColor())
                .backgroundColor(request.getBackgroundColor())
                .build();
    }

    public ApplicationColorResponse colorDtoToApplicationColorResponse(ColorDto colorDto){
        return ApplicationColorResponse.builder()
                .primaryColor(colorDto.getPrimaryColor())
                .backgroundColor(colorDto.getBackgroundColor())
                .build();
    }

}
