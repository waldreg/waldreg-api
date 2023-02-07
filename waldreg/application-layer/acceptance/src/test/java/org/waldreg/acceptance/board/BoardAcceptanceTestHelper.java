package org.waldreg.acceptance.board;


import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.waldreg.controller.board.comment.request.CommentRequest;

public class BoardAcceptanceTestHelper{


    private final static String apiVersion = "1.0";

    public static ResultActions createBoardWithAll(MockMvc mvc, String token, MockMultipartFile jsonContent, List<MockMultipartFile> imgFile, List<MockMultipartFile> docxFile) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.multipart("/board")
                                   .file(jsonContent)
                                   .file(imgFile.get(0))
                                   .file(docxFile.get(0))
                                   .header(HttpHeaders.AUTHORIZATION, token)
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

    public static ResultActions createBoardWithJsonAndImage(MockMvc mvc, String token, MockPart jsonContent, List<MockMultipartFile> imgFile) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.multipart("/board")
                                   .part(jsonContent)
                                   .file(imgFile.get(0))
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions createBoardWithJsonAndFile(MockMvc mvc, String token, MockPart jsonContent, List<MockMultipartFile> docxFile) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.multipart("/board")
                                   .part(jsonContent)
                                   .file(docxFile.get(0))
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions modifyBoardWithAll(MockMvc mvc, String token, int boardId, MockPart jsonContent, List<MockMultipartFile> imgFile, List<MockMultipartFile> docxFile) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.multipart("/board/{board-id}", boardId)
                                   .part(jsonContent)
                                   .file(imgFile.get(0))
                                   .file(docxFile.get(0))
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions modifyBoardWithOnlyJson(MockMvc mvc, String token, int boardId, MockPart jsonContent) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.multipart("/board/{board-id}", boardId)
                                   .part(jsonContent)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions modifyBoardWithJsonAndImage(MockMvc mvc, String token, int boardId, MockPart jsonContent, List<MockMultipartFile> imgFile) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.multipart("/board/{board-id}", boardId)
                                   .part(jsonContent)
                                   .file(imgFile.get(0))
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions modifyBoardWithJsonAndFile(MockMvc mvc, String token, int boardId, MockPart jsonContent, List<MockMultipartFile> docxFile) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.multipart("/board/{board-id}", boardId)
                                   .part(jsonContent)
                                   .file(docxFile.get(0))
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions deleteBoard(MockMvc mvc, String token, int boardId) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.delete("/board/{board-id}", boardId)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion)
        );
    }


    public static ResultActions inquiryAllBoard(MockMvc mvc, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/boards?from=1&to=4")
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }


    public static ResultActions inquiryAllBoardWithCategory(MockMvc mvc, String token, int category) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/boards?from=1&to=4")
                                   .param("category-id", Integer.toString(category))
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions inquiryAllBoardWithFromTo(MockMvc mvc, String token, int startIdx, int endIdx) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/boards?from=1&to=4")
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

    public static ResultActions searchBoard(MockMvc mvc, String token, String type, String keyword, int startIdx, int endIdx) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/board/search")
                                   .param("type", type)
                                   .param("keyword", keyword)
                                   .param("from", Integer.toString(startIdx))
                                   .param("to", Integer.toString(endIdx))
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions createComment(MockMvc mvc, String token, int boardId, CommentRequest commentRequest) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.post("/comment/{board-id}", boardId)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion)
                                   .contentType(MediaType.APPLICATION_JSON)
                                   .content(commentRequest.getContent()));
    }

    public static ResultActions inquiryComment(MockMvc mvc, String token, int boardId, int startIdx, int endIdx) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.post("/board/comment/{board-id}", boardId)
                                   .param("from", Integer.toString(startIdx))
                                   .param("to", Integer.toString(endIdx))
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions modifyComment(MockMvc mvc, String token, int commentId, CommentRequest commentRequest) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.put("/comment/{comment-id}", commentId)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion).contentType(MediaType.APPLICATION_JSON)
                                   .content(commentRequest.getContent()));
    }

    public static ResultActions deleteComment(MockMvc mvc, String token, int commentId) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.delete("/comment/{comment-id}", commentId)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion).contentType(MediaType.APPLICATION_JSON)
        );
    }

    public static ResultActions createReaction(MockMvc mvc, String token, int boardId, String type) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.delete("/reaction/{board-id}", boardId)
                                   .param("reaction-type", type)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion).contentType(MediaType.APPLICATION_JSON)
        );
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

    public static ResultActions getImage(MockMvc mvc, String token, String url) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get(url)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions downloadFile(MockMvc mvc, String token, String url) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get(url)
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

    public static ResultActions inquiryMemberTier(MockMvc mvc, String token) throws Exception{
        return mvc.perform(MockMvcRequestBuilders.get("/board-tier")
                                   .header(HttpHeaders.AUTHORIZATION, token)
                                   .accept(MediaType.APPLICATION_JSON)
                                   .header("api-version", apiVersion));
    }

}
