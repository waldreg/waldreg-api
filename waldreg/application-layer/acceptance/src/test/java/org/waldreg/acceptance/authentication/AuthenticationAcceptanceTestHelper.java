package org.waldreg.acceptance.authentication;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class AuthenticationAcceptanceTestHelper{

    private final static String apiVersion = "1.0";

    public static ResultActions authenticateByUserIdAndUserPassword(MockMvc mvc, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders
                                   .post("/token")
                                   .accept(MediaType.APPLICATION_JSON)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .header("api-version",apiVersion)
                                   .content(content)
                );
    }

}
