package org.waldreg.acceptance.user;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class UserAcceptanceTestHelper{

    private final static String apiVersion = "1.0";

    public static ResultActions createUser(MockMvc mvc, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .content(content));
    }

    public static ResultActions inquiryUserWithToken(MockMvc mvc, String userId, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/user/{user_id}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions inquiryUserWithoutToken(MockMvc mvc, String userId) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/user/{user_id}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion));
    }

    public static ResultActions inquiryUserOnline(MockMvc mvc, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/user")
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions inquiryUserOnlineWithNoToken(MockMvc mvc) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/user")
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion));
    }

    public static ResultActions modifyUserWithToken(MockMvc mvc, String token, String password, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .patch("/user", content)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("password", password)
                .content(content));
    }

    public static ResultActions modifyUserWithoutToken(MockMvc mvc, String password, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .patch("/user", content)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header("password", password)
                .content(content));
    }

    public static ResultActions securedDeleteUserWithToken(MockMvc mvc, String token, String password) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .delete("/user")
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("password", password));
    }

    public static ResultActions securedDeleteUserWithoutToken(MockMvc mvc, String password) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .delete("/user")
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header("password", password));
    }

    public static ResultActions forcedDeleteUserWithToken(MockMvc mvc, int id, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .delete("/user/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions forcedDeleteUserWithoutToken(MockMvc mvc, int id) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .delete("/user/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion));
    }

    public static ResultActions modifyUserCharacter(MockMvc mvc, int id, String token, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .put("/user/character/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(content));
    }

    public static ResultActions inquiryAllUserWithToken(MockMvc mvc, int startIdx, int endIdx, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/user?from={start-idx}&to={end-idx}", startIdx, endIdx)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions inquiryAllUserWithoutToken(MockMvc mvc, int startIdx, int endIdx) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/user?from={start-idx}&to={end-idx}", startIdx, endIdx)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion));
    }

}
