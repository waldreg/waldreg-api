package org.waldreg.acceptance.reward;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class RewardAcceptanceTestHelper{

    private static final String API_VERSION = "1.0";

    public static ResultActions createRewardTag(MockMvc mvc, String token, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .post("/reward-tag")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Api-version", API_VERSION)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(content));
    }

    public static ResultActions inquiryRewardTagList(MockMvc mvc, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/reward-tag")
                .accept(MediaType.APPLICATION_JSON)
                .header("Api-version", "1.0")
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions updateRewardTag(MockMvc mvc, String token, int rewardTagId, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .put("/reward-tag/{reward-tag-id}", rewardTagId)
                .accept(MediaType.APPLICATION_JSON)
                .header("Api-version", "1.0")
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
    }

    public static ResultActions deleteRewardTag(MockMvc mvc, String token, int rewardTagId) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .delete("/reward-tag/{reward-tag-id}", rewardTagId)
                .accept(MediaType.APPLICATION_JSON)
                .header("Api-version", "1.0")
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions givenRewardTagToUser(MockMvc mvc, String token, String ids, int rewardTagId) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/reward-tag/users?id={ids}&reward-tag-id={rewardTagId}", ids, rewardTagId)
                .accept(MediaType.APPLICATION_JSON)
                .header("Api-version", "1.0")
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions resetRewardTagFromAllUsers(MockMvc mvc, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/reward-tag/users/reset")
                .accept(MediaType.APPLICATION_JSON)
                .header("Api-version", "1.0")
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions inquirySpecifyUsersRewardTags(MockMvc mvc, String token, int id) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/reward-tag/user/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header("Api-version", "1.0")
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions deleteSpecifyUsersRewardTags(MockMvc mvc, String token, int id, int rewardId) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .delete("/reward-tag/users?id={id}$reward-tag={reward-id}", id, rewardId)
                .accept(MediaType.APPLICATION_JSON)
                .header("Api-version", "1.0")
                .header(HttpHeaders.AUTHORIZATION, token));
    }

}
