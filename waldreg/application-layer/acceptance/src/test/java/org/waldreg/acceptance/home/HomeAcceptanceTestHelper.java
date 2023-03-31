package org.waldreg.acceptance.home;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class HomeAcceptanceTestHelper{

    private final static String apiVersion = "1.0";

    public static ResultActions updateHomeContent(MockMvc mvc, String token, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.post("/application/home")
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion)
                                   .content(content));
    }

    public static ResultActions inquiryHomeContent(MockMvc mvc, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/application/home")
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions updateApplicationColor(MockMvc mvc, String token, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.post("/application/setting/color")
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion)
                                   .content(content));
    }

    public static ResultActions inquiryApplicationColor(MockMvc mvc, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/application/setting/color")
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions updateLogo(MockMvc mvc, String token, MockMultipartFile file) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.multipart("/application/setting/logo")
                                   .file(file)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));

    }

    public static ResultActions inquiryLogo(MockMvc mvc, String token, String url) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get(url)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }


}
