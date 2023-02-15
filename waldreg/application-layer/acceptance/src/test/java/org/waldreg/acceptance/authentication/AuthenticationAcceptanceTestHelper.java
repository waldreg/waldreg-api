package org.waldreg.acceptance.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.waldreg.auth.request.AuthTokenRequest;
import org.waldreg.auth.response.AuthTokenResponse;
import org.waldreg.auth.response.TemporaryTokenResponse;

public class AuthenticationAcceptanceTestHelper{

    private final static String apiVersion = "1.0";

    public static String getAdminToken(MockMvc mvc, ObjectMapper objectMapper) throws Exception{
        AuthTokenRequest authTokenRequest = AuthTokenRequest.builder()
                .userId("Admin")
                .userPassword("0000")
                .build();
        String content = AuthenticationAcceptanceTestHelper
                .authenticateByUserIdAndUserPassword(mvc, objectMapper.writeValueAsString(authTokenRequest))
                .andReturn()
                .getResponse()
                .getContentAsString();
        AuthTokenResponse response = objectMapper.readValue(content, AuthTokenResponse.class);

        return response.getTokenType() + " " + response.getAccessToken();
    }

    public static String getToken(MockMvc mvc, ObjectMapper objectMapper, AuthTokenRequest authTokenRequest) throws Exception{
        String content = AuthenticationAcceptanceTestHelper
                .authenticateByUserIdAndUserPassword(mvc, objectMapper.writeValueAsString(authTokenRequest))
                .andReturn()
                .getResponse()
                .getContentAsString();

        AuthTokenResponse response = objectMapper.readValue(content, AuthTokenResponse.class);
        return response.getTokenType() + " " + response.getAccessToken();
    }

    public static ResultActions authenticateByUserIdAndUserPassword(MockMvc mvc, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                                   .post("/token")
                                   .accept(MediaType.APPLICATION_JSON)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion)
                                   .content(content)
        );
    }

    public static ResultActions authenticateByToken(MockMvc mvc, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                                   .get("/token")
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion)
                                   .header(HttpHeaders.AUTHORIZATION, token)
        );
    }

}
