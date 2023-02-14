package org.waldreg.acceptance.attendance;

import java.time.LocalDate;
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

    public static ResultActions modifyAttendance(MockMvc mvc, String token, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.post("/attendance/status")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", API_VERSION)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(content));
    }

    public static ResultActions readAttendanceUsers(MockMvc mvc, String token, LocalDate from, LocalDate to) throws Exception{
        return readAttendanceUsers(mvc, token, from.toString(), to.toString());
    }

    public static ResultActions readAttendanceUsers(MockMvc mvc, String token, String from, String to) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/attendance/calendar?from={from}&to={to}", from, to)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", API_VERSION)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions readSelfAttendance(MockMvc mvc, String token, LocalDate from, LocalDate to) throws Exception{
        return readSelfAttendance(mvc, token, from.toString(), to.toString());
    }

    public static ResultActions readSelfAttendance(MockMvc mvc, String token, String from, String to) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/attendance/user?from={from}&to={to}", from, to)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", API_VERSION)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

    public static ResultActions confirmAttendance(MockMvc mvc, String token, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.post("/attendance/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", API_VERSION)
                .header(HttpHeaders.AUTHORIZATION, token)
                .content(content));
    }

    public static ResultActions setAttendanceTagsReward(MockMvc mvc, String token, AttendanceType attendanceType, int rewardTagId) throws Exception{
        return setAttendanceTagsReward(mvc, token, attendanceType.toString(), rewardTagId);
    }

    public static ResultActions setAttendanceTagsReward(MockMvc mvc, String token, String attendanceType, int rewardTagId) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                .post("/attendance/reward-tag?attendance-type={attendance-type}&reward-tag-id={reward-tag-id}", attendanceType, rewardTagId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("api-version", API_VERSION)
                .header(HttpHeaders.AUTHORIZATION, token));
    }

}
