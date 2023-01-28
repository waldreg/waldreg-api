package org.waldreg.acceptance.board;


import org.mockito.Mock;
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

    public static ResultActions modifyBoardWithAll(MockMvc mvc, String token, int boardId, MockPart jsonContent, MockMultipartFile imgFile, MockMultipartFile docxFile) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.multipart("/board/{board-id}", boardId)
                                   .part(jsonContent)
                                   .file(imgFile)
                                   .file(docxFile)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions deleteBoard(MockMvc mvc, String token, int boardId) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.delete("/board/{board-id}",boardId)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion)
        );
    }


    public static ResultActions inquiryAllBoard(MockMvc mvc, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/boards")
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }


    public static ResultActions inquiryAllBoardWithCategory(MockMvc mvc, String token, int category) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/boards")
                                   .param("category", Integer.toString(category))
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions inquiryAllBoardWithFromTo(MockMvc mvc, String token, int startIdx, int endIdx) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/boards")
                                   .param("from", Integer.toString(startIdx))
                                   .param("to", Integer.toString(endIdx))
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions inquiryAllBoardWithCategoryAndFromTo(MockMvc mvc, String token, int category, int startIdx, int endIdx) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/boards")
                                   .param("category", Integer.toString(category))
                                   .param("from", Integer.toString(startIdx))
                                   .param("to", Integer.toString(endIdx))
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions inquiryBoardById(MockMvc mvc, String token, int id) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/board/{id}", id)
                                   .header(HttpHeaders.AUTHORIZATION, token)
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

    public static ResultActions inquiryAllCategory(MockMvc mvc, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/category")
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion)
        );
    }

    public static ResultActions modifyCategory(MockMvc mvc, String token, int categoryId, String content) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.put("/category/{category-id}", categoryId)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion)
                                   .content(content));
    }

    public static ResultActions deleteCategory(MockMvc mvc, String token, int categoryId) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.delete("/category/{category-id}", categoryId)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

}
