package org.waldreg.acceptance.permission;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class PermissionAcceptanceTestHelper{

    private final static String apiVersion = "1.0";

    public static ResultActions createCharacter(MockMvc mvc, String token, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .post("/character")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(content));
    }

    public static ResultActions inquiryCharacterList(MockMvc mvc, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/character")
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions inquirySpecificCharacter(MockMvc mvc,
            String characterName, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/character/{character-name}", characterName)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions modifySpecificCharacter(MockMvc mvc,
            String characterName, String token, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .patch("/character/{character-name}", characterName)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(content));
    }

    public static ResultActions deleteSpecificCharacter(MockMvc mvc,
            String characterName, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .delete("/character/{character-name}", characterName)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions inquiryPermissionList(MockMvc mvc, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/permission")
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

}
