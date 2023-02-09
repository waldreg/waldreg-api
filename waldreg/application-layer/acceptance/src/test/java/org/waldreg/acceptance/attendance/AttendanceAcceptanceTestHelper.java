package org.waldreg.acceptance.attendance;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.waldreg.acceptance.attendance.AttendanceAcceptanceTest.AttendanceType;

public class AttendanceAcceptanceTestHelper{

    private final static String API_VERSION = "1.0";

    public static ResultActions subscribeAttendance(MockMvc mvc, String token, String userIdList) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/attendance/subscribed?user-id={userIdList}", userIdList)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", API_VERSION)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions deleteSubscribedAttendance(MockMvc mvc, String token, String userIdList) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.delete("/attendance/subscribed?user-id={userIdList}", userIdList)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", API_VERSION)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions amIAttendanceTarget(MockMvc mvc, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/attendance")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", API_VERSION)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions waiver(MockMvc mvc, String token, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.post("/attendance/waiver")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", API_VERSION)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(content));
    }

    public static ResultActions readWaiver(MockMvc mvc, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/attendance/waiver")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", API_VERSION)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions acceptWaiver(MockMvc mvc, String token, int waiverId, AttendanceType waiverType) throws Exception{
        return acceptWaiver(mvc, token, waiverId, waiverType.toString());
    }

    public static ResultActions acceptWaiver(MockMvc mvc, String token, int waiverId, String waiverType) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/attendance/waiver/{waiver-id}/{waiver-type}", waiverId, waiverType)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", API_VERSION)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

}
