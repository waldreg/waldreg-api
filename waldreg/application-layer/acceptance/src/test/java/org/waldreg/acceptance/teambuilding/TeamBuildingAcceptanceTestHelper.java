package org.waldreg.acceptance.teambuilding;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class TeamBuildingAcceptanceTestHelper{

    private final static String apiVersion = "1.0";

    public static ResultActions createTeamBuilding(MockMvc mvc, String content, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .post("/teambuilding")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(content));

    }

    public static ResultActions inquiryAllTeamBuilding(MockMvc mvc, int startIdx, int endIdx, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/teambuilding")
                .queryParam("from", Integer.toString(startIdx))
                .queryParam("to", Integer.toString(endIdx))
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions inquirySpecificTeamBuilding(MockMvc mvc, int teamBuildingId, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/teambuilding/{teambuilding-id}", teamBuildingId)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions addNewTeam(MockMvc mvc, int teamBuildingId, String content, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .post("/teambuilding/{teambuilding-id}", teamBuildingId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(content));
    }

    public static ResultActions modifyTeamInTeamBuilding(MockMvc mvc, int teamId, String content, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .patch("/teambuilding/{team-id}", teamId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(content));
    }

    public static ResultActions modifyTeamBuilding(MockMvc mvc, int teamBuildingId, String content, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .put("/teambuilding/{teambuilding-id}", teamBuildingId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(content));
    }

    public static ResultActions modifyTeamNameInTeamBuilding(MockMvc mvc, int teamId, String content, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .patch("/teambuilding/{team-id}", teamId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(content));
    }

    public static ResultActions deleteTeamBuilding(MockMvc mvc, int teamBuildingId, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .delete("/teambuilding", teamBuildingId)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions deleteTeamInTeamBuilding(MockMvc mvc, int teamId, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .patch("/teambuilding/{team_id}", teamId)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

}
