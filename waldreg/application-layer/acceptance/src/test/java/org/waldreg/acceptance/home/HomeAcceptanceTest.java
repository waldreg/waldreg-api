package org.waldreg.acceptance.home;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.waldreg.acceptance.authentication.AuthenticationAcceptanceTestHelper;
import org.waldreg.controller.home.request.ApplicationColorRequest;
import org.waldreg.controller.home.request.HomeContentRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class HomeAcceptanceTest{

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final String apiVersion = "1.0";

    @BeforeEach
    @AfterEach
    void INIT_LOGO_IMAGE(){

    }

    @Test
    @DisplayName("홈 내용 수정 성공")
    void UPDATE_HOME_CONTENT() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String modifyContent = "modified content";
        HomeContentRequest request = HomeContentRequest.builder().content(modifyContent).build();

        //when
        ResultActions result = HomeAcceptanceTestHelper.updateHomeContent(mvc, adminToken, objectMapper.writeValueAsString(request));
        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("홈 내용 조회 성공")
    void INQUIRY_HOME_CONTENT_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String content = "content";
        HomeContentRequest request = HomeContentRequest.builder().content(content).build();
        HomeAcceptanceTestHelper.updateHomeContent(mvc, adminToken, objectMapper.writeValueAsString(request));

        //when
        ResultActions result = HomeAcceptanceTestHelper.inquiryHomeContent(mvc, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.content").value(content)
        );
    }

    @Test
    @DisplayName("어플리케이션 컬러 수정 성공")
    void UPDATE_APPLICATION_COLOR_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String primaryColor = "#1FDEA2";
        String backgroundColor = "#1FD5A8";

        ApplicationColorRequest request = ApplicationColorRequest.builder()
                .primaryColor(primaryColor)
                .backgroundColor(backgroundColor)
                .build();

        //when
        ResultActions result = HomeAcceptanceTestHelper.updateApplicationColor(mvc, adminToken, objectMapper.writeValueAsString(request));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("어플리케이션 컬러 조회 성공")
    void INQUIRY_APPLICATION_COLOR_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String primaryColor = "#1FDEA2";
        String backgroundColor = "#1FD5A8";
        ApplicationColorRequest request = ApplicationColorRequest.builder()
                .primaryColor(primaryColor)
                .backgroundColor(backgroundColor)
                .build();
        HomeAcceptanceTestHelper.updateApplicationColor(mvc, adminToken, objectMapper.writeValueAsString(request));

        //when
        ResultActions result = HomeAcceptanceTestHelper.inquiryApplicationColor(mvc, adminToken);
        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.primary_color").value(primaryColor),
                MockMvcResultMatchers.jsonPath("$.background_color").value(backgroundColor)
        );
    }

    @Test
    @DisplayName("로고 수정 성공")
    void UPDATE_LOGO_SUCCESS_TEST()throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String logoName = "TestLogo.svg";
        String logoContentType = "image/svg+xml";
        String logoPath = "./src/test/java/org/waldreg/acceptance/home/waldreg.svg";
        MockMultipartFile logoFile = new MockMultipartFile("logo", logoName, logoContentType, new FileInputStream(logoPath));
        //when
        ResultActions result = HomeAcceptanceTestHelper.updateLogo(mvc, adminToken, logoFile);
        //then
        result.andExpectAll(
            MockMvcResultMatchers.status().isOk(),
            MockMvcResultMatchers.header().string("api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("로고 조회 성공")
    void INQUIRY_LOGO_SUCCESS_TEST()throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String logoName = "TestLogo.svg";
        String logoContentType = "image/svg+xml";
        String logoPath = "./src/test/java/org/waldreg/acceptance/home/waldreg.svg";
        MockMultipartFile logoFile = new MockMultipartFile("logo", logoName, logoContentType, new FileInputStream(logoPath));
        HomeAcceptanceTestHelper.updateLogo(mvc, adminToken, logoFile);
        String url = "Logo.svg";
        //when
        ResultActions result = HomeAcceptanceTestHelper.inquiryLogo(mvc,adminToken,"/logo/" + url );

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

}
