package org.waldreg.controller.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.stage.xss.core.meta.Xss;
import org.stage.xss.core.meta.XssFiltering;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.controller.home.mapper.ControllerHomeMapper;
import org.waldreg.controller.home.request.ApplicationColorRequest;
import org.waldreg.controller.home.request.HomeContentRequest;
import org.waldreg.controller.home.response.ApplicationColorResponse;
import org.waldreg.controller.home.response.HomeContentResponse;
import org.waldreg.home.color.dto.ColorDto;
import org.waldreg.home.color.management.ColorManager;
import org.waldreg.home.homecontent.dto.HomeContentDto;
import org.waldreg.home.homecontent.management.HomeManager;
import org.waldreg.home.logo.management.LogoManager;
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
        homeManager.updateHome(homeContentDto);
    }

    @Authenticating
    @GetMapping(value = "/application/home")
    public HomeContentResponse getHomeContent(){
        HomeContentDto homeContentDto = homeManager.getHome();
        return controllerHomeMapper.HomeContentDtoToHomeContentResponse(homeContentDto);
    }

    @XssFiltering
    @Authenticating
    @PermissionVerifying("")
    @PostMapping(value = "/application/setting/color")
    public void updateApplicationColor(@Xss("json") @RequestBody @Validated ApplicationColorRequest applicationColorRequest){
        ColorDto colorDto = controllerHomeMapper.colorRequestToApplicationColorDto(applicationColorRequest);
        colorManager.updateColor(colorDto);
    }

    @Authenticating
    @GetMapping(value = "/application/setting/color")
    public ApplicationColorResponse getApplicationColor(){
        ColorDto colorDto = colorManager.getColor();
        return controllerHomeMapper.colorDtoToApplicationColorResponse(colorDto);
    }

    @Authenticating
    @PermissionVerifying("")
    @PostMapping(value = "/application/setting/logo")
    public void updateLogo(@RequestPart(value = "logo")MultipartFile file){
        logoManager.updateLogo(file);
    }

    @Authenticating
    @GetMapping(value = "/application/setting/logo")
    public @ResponseBody byte[] getLogo(){
        return logoManager.getLogo();
    }
}
