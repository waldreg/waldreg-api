package org.waldreg.acceptance.user;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class UserAcceptanceTestHelper{

    private final static String apiVersion = "1.0";

    public static ResultActions createUser(MockMvc mvc, String url, String content)
            throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .content(content));
    }

    public static ResultActions inquiryUserWithToken(MockMvc mvc, String url, String name,
            String token)
            throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get(url, name)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions inquiryUserWithoutToken(MockMvc mvc, String url, String name)
            throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get(url, name)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion));
    }

    public static ResultActions inquiryUserOnline(MockMvc mvc, String url, String token)
            throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get(url)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions inquiryUserOnlineWithNoToken(MockMvc mvc, String url)
            throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get(url)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion));
    }

    public static ResultActions modifyUserWithToken(MockMvc mvc, String url, String token,
            String password,
            String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .patch(url, content)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("password", password)
                .content(content));
    }

    public static ResultActions modifyUserWithoutToken(MockMvc mvc, String url, String password,
            String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .patch(url, content)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header("password", password)
                .content(content));
    }

    public static ResultActions deleteUserWithToken(MockMvc mvc, String url, String token,
            String password)
            throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .delete(url)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token)
                .header("password", password));
    }

    public static ResultActions deleteUserWithoutToken(MockMvc mvc, String url, String password)
            throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .delete(url)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header("password", password));
    }

}
