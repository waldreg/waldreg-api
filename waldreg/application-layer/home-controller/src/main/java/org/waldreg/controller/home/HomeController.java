package org.waldreg.controller.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.stage.xss.core.meta.Xss;
import org.stage.xss.core.meta.XssFiltering;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.controller.home.mapper.ControllerHomeMapper;
import org.waldreg.controller.home.request.ApplicationColorRequest;
import org.waldreg.controller.home.request.HomeContentRequest;
import org.waldreg.controller.home.response.ApplicationColorResponse;
import org.waldreg.controller.home.response.HomeContentResponse;
import org.waldreg.home.core.ColorManager;
import org.waldreg.home.core.HomeManager;
import org.waldreg.home.core.LogoManager;
import org.waldreg.token.aop.annotation.Authenticating;

public class HomeController{

    private final HomeManager homeManager;
    private final ColorManager colorManager;
    private final LogoManager logoManager;

    private final ControllerHomeMapper controllerHomeMapper;

    @Autowired
    public HomeController(HomeManager homeManager, ColorManager colorManager, LogoManager logoManager, ControllerHomeMapper controllerHomeMapper){
        this.homeManager = homeManager;
        this.colorManager = colorManager;
        this.logoManager = logoManager;
        this.controllerHomeMapper = controllerHomeMapper;
    }

    @XssFiltering
    @Authenticating
    @PermissionVerifying("")
    @PostMapping(value = "/application/home")
    public void updateHomeContent(@Xss("json") @RequestBody @Validated HomeContentRequest homeContentRequest){
        HomeContentDto homeContentDto = controllerHomeMapper.HomeContentRequestToHomeContentDto(homeContentRequest);
        homeManager.updateContent(homeContentDto);
    }

    @Authenticating
    @GetMapping(value = "/application/home")
    public HomeContentResponse getHomeContent(){
        HomeContentDto homeContentDto = homeManager.getHomeContent();
        return controllerHomeMapper.HomeContentDtoToHomeContentResponse(homeContentDto);
    }

    @XssFiltering
    @Authenticating
    @PermissionVerifying("")
    @PostMapping(value = "/application/setting/color")
    public void updateApplicationColor(@Xss("json") @RequestBody @Validated ApplicationColorRequest applicationColorRequest){
        ApplicationColorDto applicationColorDto = controllerHomeMapper.ApplicationColorRequestToApplicationColorDto(applicationColorRequest);
        colorManager.updateColor(applicationColorDto);
    }

    @Authenticating
    @GetMapping(value = "/application/setting/color")
    public ApplicationColorResponse getApplicationColor(){
        ApplicationColorDto applicationColorDto = colorManager.getColor();
        return controllerHomeMapper.applicationColorDtoToApplicationColorResponse(applicationColorDto);
    }

    @Authenticating
    @PermissionVerifying("")
    @PostMapping(value = "/application/setting/logo")
    public void updateLogo(@RequestPart(value = "logo")MultipartFile file){
        logoManager.updateLogo(file);
    }

    @Authenticating
    @GetMapping(value = "/application/setting/logo")
    public void getLogo(){

        return logoManager.getLogo();
    }
dasf
}
