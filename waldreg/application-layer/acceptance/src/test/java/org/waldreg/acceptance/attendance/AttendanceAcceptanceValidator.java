package org.waldreg.acceptance.attendance;

import java.time.LocalDate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class AttendanceAcceptanceValidator{

    private static final String API_VERSION = "1.0";

    public static void expectedIsOk(ResultActions resultActions) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", API_VERSION)
        );
    }

    public static void expectedIsNoPermission(ResultActions resultActions) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", API_VERSION),
                MockMvcResultMatchers.jsonPath("$.code").value("CHARACTER-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    public static void expectedIsUnknownUserId(ResultActions resultActions, int id) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", API_VERSION),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-413"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user id \"" + id + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    public static void expectedIsNotRegisteredOnAttendance(ResultActions resultActions) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", API_VERSION),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-410"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Not registered attendance list"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    public static void expectedIsInvalidWaiverDate(ResultActions resultActions) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", API_VERSION),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-417"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid waiver date"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    public static void expectedIsTooEarlyDate(ResultActions resultActions, LocalDate date) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", API_VERSION),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-416"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Date is too early \"" + date.toString() + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    public static void expectedIsTooFarDate(ResultActions resultActions, LocalDate date) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", API_VERSION),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-415"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Date is too far \"" + date.toString() + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    public static void expectedIsUnknownWaiverId(ResultActions resultActions, int waiverId) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", API_VERSION),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-411"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown waiver id \"" + waiverId + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    public static void expectedIsUnknownAttendanceType(ResultActions resultActions, String attendanceType) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", API_VERSION),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-412"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown attendance type \"" + attendanceType + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    public static void expectedIsInvalidDate(ResultActions resultActions) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", API_VERSION),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-414"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid date"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    public static void expectedIsAlreadyAttendanced(ResultActions resultActions) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", API_VERSION),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-418"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Already attendance"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    public static void expectedIsWrongNumber(ResultActions resultActions, String number) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", API_VERSION),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-419"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Wrong attendance identify \"" + number + "\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    public static void expectedIsNotStartedAttendance(ResultActions resultActions) throws Exception{
        resultActions.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", API_VERSION),
                MockMvcResultMatchers.jsonPath("$.code").value("ATTENDANCE-421"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Does not started attendance"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

}
