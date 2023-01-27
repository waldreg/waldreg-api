package org.waldreg.acceptance.board;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

public class BoardAcceptanceTestHelper{


    private final static String apiVersion = "1.0";

    public static ResultActions createBoardWithAll(MockMvc mvc, String token, MockPart jsonContent, MockMultipartFile imgFile, MockMultipartFile docxFile) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.multipart("/board")
                                   .part(jsonContent)
                                   .file(imgFile)
                                   .file(docxFile)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions createBoardWithOnlyJson(MockMvc mvc, String token, MockPart jsonContent) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.multipart("/board")
                                   .part(jsonContent)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions createBoardWithJsonAndImage(MockMvc mvc, String token, MockPart jsonContent, MockMultipartFile imgFile) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.multipart("/board")
                                   .part(jsonContent)
                                   .file(imgFile)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions createBoardWithJsonAndFile(MockMvc mvc, String token, MockPart jsonContent, MockMultipartFile docxFile) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.multipart("/board")
                                   .part(jsonContent)
                                   .file(docxFile)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }


    public static ResultActions createCategory(MockMvc mvc, String token, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.post("/category")
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion)
                                   .content(content));
    }

}
