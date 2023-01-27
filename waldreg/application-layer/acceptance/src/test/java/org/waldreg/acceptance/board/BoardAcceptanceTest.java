package org.waldreg.acceptance.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.waldreg.acceptance.authentication.AuthenticationAcceptanceTestHelper;
import org.waldreg.acceptance.user.UserAcceptanceTestHelper;
import org.waldreg.auth.request.AuthTokenRequest;
import org.waldreg.controller.board.request.BoardCreateRequest;
import org.waldreg.controller.board.request.CategoryCreateRequest;
import org.waldreg.controller.board.response.BoardResponse;
import org.waldreg.controller.user.request.UserRequest;
import org.waldreg.controller.user.response.UserResponse;

@SpringBootTest
@AutoConfigureMockMvc
public class BoardAcceptanceTest{

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String apiVersion = "1.0";

    private final ArrayList<UserRequest> userCreateRequestList = new ArrayList<>();

    private final ArrayList<BoardResponse> boardResponseList = new ArrayList<>();

    public BoardAcceptanceTest() throws Exception{}

    @BeforeEach
    @AfterEach
    public void INITIATE_USER() throws Exception{
        String url = "/user/{id}";
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        for (UserRequest request : userCreateRequestList){
            UserResponse userResponse = objectMapper.readValue(UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, request.getUserId())
                                                                       .andReturn()
                                                                       .getResponse()
                                                                       .getContentAsString(), UserResponse.class);
            UserAcceptanceTestHelper.forcedDeleteUserWithToken(mvc, userResponse.getId(), adminToken);
            ResultActions result = UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, request.getUserId());
            result.andExpectAll(
                    MockMvcResultMatchers.status().isBadRequest(),
                    MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                    MockMvcResultMatchers.header().string("api-version", apiVersion),
                    MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                    MockMvcResultMatchers.jsonPath("$.messages").value("Unknown user_id"),
                    MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
            );
        }
        userCreateRequestList.clear();
    }

    @BeforeEach
    @AfterEach
    public void INITIATE_Board() throws Exception{
        String url = "/boards";
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        for (BoardResponse response : boardResponseList){
            int boardId = response.getId();

            //보드 삭제 메서드

        }
        boardResponseList.clear();
    }


    @Test
    @DisplayName("게시글 생성 성공(이미지,파일 포함)")
    public void CREATE_BOARD_WITH_ALL_TEST() throws Exception{

        //given
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "notice";
        String content = "content";
        String category = "cate1";
        String memberTier = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).category(category).memberTier(memberTier).build();

        String imgName = "TestImage.jpg";
        String imgContentType = "jpg";
        String imgPath = "./src/test/java/org/waldreg/acceptance/board/TestImage.jpg";

        String fileName = "TestDocx";
        String fileContentType = "docx";
        String filePath = "./src/test/java/org/waldreg/acceptance/board/TestDocx.docx";

        MockPart jsonContent = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        MockMultipartFile imgFile = new MockMultipartFile("image", imgName, imgContentType, new FileInputStream(imgPath));
        MockMultipartFile docxFile = new MockMultipartFile("file", fileName, fileContentType, new FileInputStream(filePath));
        //when

        ResultActions result = BoardAcceptanceTestHelper.createBoardWithAll(mvc, token, jsonContent, imgFile, docxFile);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("게시글 생성 성공(json 객체만)")
    public void CREATE_BOARD_WITH_ONLY_JSON_TEST() throws Exception{

        //given
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "notice";
        String content = "content";
        String category = "cate1";
        String memberTier = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).category(category).memberTier(memberTier).build();

        MockPart jsonContent = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        //when

        ResultActions result = BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("게시글 생성 성공(json과 이미지 포함)")
    public void CREATE_BOARD_WITH_JSON_AND_IMAGE_TEST() throws Exception{

        //given
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "notice";
        String content = "content";
        String category = "cate1";
        String memberTier = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).category(category).memberTier(memberTier).build();

        String imgName = "TestImage.jpg";
        String imgContentType = "jpg";
        String imgPath = "./src/test/java/org/waldreg/acceptance/board/TestImage.jpg";

        MockPart jsonContent = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        MockMultipartFile imgFile = new MockMultipartFile("image", imgName, imgContentType, new FileInputStream(imgPath));
        //when

        ResultActions result = BoardAcceptanceTestHelper.createBoardWithJsonAndImage(mvc, token, jsonContent, imgFile);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("게시글 생성 성공(json과 파일 포함)")
    public void CREATE_BOARD_WITH_JSON_AND_FILE_TEST() throws Exception{

        //given
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "notice";
        String content = "content";
        String category = "cate1";
        String memberTier = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).category(category).memberTier(memberTier).build();

        String fileName = "TestDocx";
        String fileContentType = "docx";
        String filePath = "./src/test/java/org/waldreg/acceptance/board/TestDocx.docx";

        MockPart jsonContent = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        MockMultipartFile docxFile = new MockMultipartFile("file", fileName, fileContentType, new FileInputStream(filePath));
        //when

        ResultActions result = BoardAcceptanceTestHelper.createBoardWithJsonAndFile(mvc, token, jsonContent, docxFile);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("게시글 생성 실패 - tier문제로 권한이 없는경우")
    public void CREATE_BOARD_FAIL_NO_PERMISSON_BY_TIER_TEST() throws Exception{

        //given
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "notice";
        String content = "content";
        String category = "cate1";
        String memberTier = "tier 1";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).category(category).memberTier(memberTier).build();

        MockPart jsonContent = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        //when

        ResultActions result = BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    @Test
    @DisplayName("게시글 생성 실패 - 게시글 제목이 빈 제목일 경우")
    public void CREATE_BOARD_FAIL_EMPTY_TITLE_TEST() throws Exception{

        //given
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "";
        String content = "content";
        String category = "cate1";
        String memberTier = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).category(category).memberTier(memberTier).build();

        MockPart jsonContent = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        //when

        ResultActions result = BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Blank board title"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    @Test
    @DisplayName("특정 게시글 조회 성공")
    public void GET_BOARD_BY_BOARD_ID_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        //when

        //then

    }

    @Test
    @DisplayName("새로운 카테고리 생성 성공")
    public void CREATE_NEW_CATEGORY_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "photo";
        String memberTier = "tier 1";
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequest.builder().categoryName(categoryName).memberTier(memberTier).build();

        //when
        ResultActions result = BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("새로운 카테고리 생성 실패 - 권한 없는 사람의 생성 시도 ")
    public void CREATE_NEW_CATEGORY_NO_PERMISSON_TEST() throws Exception{

        //given
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String categoryName = "photo";
        String CategoryMemberTier = "tier 1";
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequest.builder().categoryName(categoryName).memberTier(CategoryMemberTier).build();

        //when
        ResultActions result = BoardAcceptanceTestHelper.createCategory(mvc, token, objectMapper.writeValueAsString(categoryCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isForbidden(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("새로운 카테고리 생성 실패 - 빈 카테고리명 ")
    public void CREATE_NEW_CATEGORY_EMPTY_CATEGORY_TITLE_TEST() throws Exception{

        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "";
        String memberTier = "tier 1";
        CategoryCreateRequest categoryCreateRequest = CategoryCreateRequest.builder().categoryName(categoryName).memberTier(memberTier).build();

        //when
        ResultActions result = BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryCreateRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("No permission"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }


    private String createUserAndGetToken(String name, String userId, String userPassword) throws Exception{
        UserRequest userRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber("010-1234-1234")
                .build();
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest)).andDo(MockMvcResultHandlers.print());
        userCreateRequestList.add(userRequest);

        return AuthenticationAcceptanceTestHelper.getToken(mvc, objectMapper, AuthTokenRequest.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build());
    }

}
