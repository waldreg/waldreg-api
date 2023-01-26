package org.waldreg.acceptance.schedule;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class ScheduleAcceptanceTestHelper{

    private static final String apiVersion = "1.0";

    public static ResultActions createNewSchedule(MockMvc mvc, String token, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .post("/schedule")
                .accept(MediaType.APPLICATION_JSON)
                .header("Api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(content));
    }

    public static ResultActions inquirySpecificSchedule(MockMvc mvc, int id, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/schedule/{schedule-id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .header("Api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions inquiryScheduleByTerm(MockMvc mvc, int year, int month, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .get("/schedule").param("year", Integer.toString(year)).param("month", Integer.toString(month))
                .accept(MediaType.APPLICATION_JSON)
                .header("Api-version", apiVersion)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

}
