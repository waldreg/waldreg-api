package org.waldreg.home.color;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.home.color.management.ColorManager;
import org.waldreg.home.color.dto.ColorDto;
import org.waldreg.home.color.management.DefaultColorManager;
import org.waldreg.home.spi.ColorRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultColorManager.class})
public class ColorServiceTest{

    @Autowired
    ColorManager colorManager;

    @MockBean
    ColorRepository colorRepository;

    @Test
    @DisplayName("애플리케이션 색깔 조회 성공 테스트")
    public void READ_APPLICATION_COLOR_SUCCESS_TEST(){
        //given
        String primaryColor = "#6600FF";
        String backgroundColor = "#FFFFFF";
        ColorDto colorDto = ColorDto.builder()
                .primaryColor(primaryColor)
                .backgroundColor(backgroundColor)
                .build();

        //when
        Mockito.when(colorRepository.getColor()).thenReturn(colorDto);
        ColorDto result = colorManager.getColor();

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(result.getPrimaryColor(),primaryColor),
                () -> Assertions.assertEquals(result.getBackgroundColor(),backgroundColor)
        );

    }

    @Test
    @DisplayName("애플리케이션 색깔 수정 성공 테스트")
    public void UPDATE_APPLICATION_COLOR_SUCCESS_TEST(){
        //given
        String updatePrimaryColor = "#FF0000";
        String updateBackgroundColor = "#FFFFFF";
        ColorDto colorDto = ColorDto.builder()
                .primaryColor(updatePrimaryColor)
                .backgroundColor(updateBackgroundColor)
                .build();

        //when
        colorManager.updateColor(colorDto);
        Mockito.when(colorRepository.getColor()).thenReturn(colorDto);
        ColorDto result = colorManager.getColor();

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(result.getPrimaryColor(),updatePrimaryColor),
                () -> Assertions.assertEquals(result.getBackgroundColor(),updateBackgroundColor)
        );

    }

}
