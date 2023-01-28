package org.waldreg.acceptance.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.waldreg.acceptance.authentication.AuthenticationAcceptanceTestHelper;
import org.waldreg.acceptance.user.UserAcceptanceTestHelper;
import org.waldreg.auth.request.AuthTokenRequest;
import org.waldreg.controller.board.request.BoardCreateRequest;
import org.waldreg.controller.board.request.BoardUpdateRequest;
import org.waldreg.controller.board.request.CategoryRequest;
import org.waldreg.controller.board.response.BoardListResponse;
import org.waldreg.controller.board.response.BoardResponse;
import org.waldreg.controller.board.response.CategoryListResponse;
import org.waldreg.controller.board.response.CategoryResponse;
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
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "notice";
        String content = "content";
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).memberTier(memberTier1).build();

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
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "notice";
        String content = "content";
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).memberTier(memberTier1).build();

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
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "notice";
        String content = "content";
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).memberTier(memberTier1).build();

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
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "notice";
        String content = "content";
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).memberTier(memberTier1).build();

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
    @DisplayName("게시글 생성 실패 - tier 문제로 권한이 없는경우")
    public void CREATE_BOARD_FAIL_NO_PERMISSION_BY_TIER_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "notice";
        String content = "content";
        String memberTier1 = "tier 1";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).memberTier(memberTier1).build();

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
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "";
        String content = "content";
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).memberTier(memberTier1).build();

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
    @DisplayName("특정 게시글 조회 성공 - json 객체만")
    public void INQUIRY_BOARD_BY_BOARD_ID_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title = "notice";
        String content = "content";
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).memberTier(memberTier1).build();
        MockPart jsonContent = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest).getBytes());

        //when
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent);
        ResultActions boards = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, token);

        BoardListResponse boardListResponse = objectMapper.readValue(boards.andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards()[0].getId();
        ResultActions result = BoardAcceptanceTestHelper.inquiryBoardById(mvc, token, boardId);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.id").value(boardId),
                MockMvcResultMatchers.jsonPath("$.title").value(title),
                MockMvcResultMatchers.jsonPath("$.content").value(content),
                MockMvcResultMatchers.jsonPath("$.category").value(categoryId),
                MockMvcResultMatchers.jsonPath("$.member_tier").value(memberTier),
                MockMvcResultMatchers.jsonPath("$.author.user_id").value(userId),
                MockMvcResultMatchers.jsonPath("$.author.name").value(name),
                MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.exist_file").value("false"),
                MockMvcResultMatchers.jsonPath("$.reactions.good").isNumber(),
                MockMvcResultMatchers.jsonPath("$.reactions.bad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.reactions.check").isNumber(),
                MockMvcResultMatchers.jsonPath("$.reactions.heart").isNumber(),
                MockMvcResultMatchers.jsonPath("$.reactions.smile").isNumber(),
                MockMvcResultMatchers.jsonPath("$.reactions.sad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.reactions.users").isArray()
        );

    }

    @Test
    @DisplayName("특정 게시글 조회 실패 - 없는 게시글 id")
    public void INQUERY_BOARD_BY_BOARD_ID_Fail_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title = "notice";
        String content = "content";
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).memberTier(memberTier1).build();
        MockPart jsonContent = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest).getBytes());

        //when
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent);
        ResultActions result = BoardAcceptanceTestHelper.inquiryBoardById(mvc, token, -1);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown board id"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")

        );

    }

    @Test
    @DisplayName("특정 게시글 조회 실패 - 권한 없는 게시글 조회")
    public void INQUIRY_BOARD_BY_BOARD_ID_FAIL_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title = "notice";
        String content = "content";
        String memberTier1 = "tier 1";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).memberTier(memberTier1).build();
        MockPart jsonContent = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest).getBytes());

        //when
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent);
        ResultActions boards = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, token);

        BoardListResponse boardListResponse = objectMapper.readValue(boards.andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards()[0].getId();
        ResultActions result = BoardAcceptanceTestHelper.inquiryBoardById(mvc, token, boardId);

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
    @DisplayName("특정 게시글 수정 성공")
    public void MODIFY_BOARD_WITH_ALL_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "notice";
        String content = "content";
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).memberTier(memberTier1).build();

        String imgName = "TestImage.jpg";
        String imgContentType = "jpg";
        String imgPath = "./src/test/java/org/waldreg/acceptance/board/TestImage.jpg";

        String fileName = "TestDocx";
        String fileContentType = "docx";
        String filePath = "./src/test/java/org/waldreg/acceptance/board/TestDocx.docx";

        MockPart jsonContent = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        MockMultipartFile imgFile = new MockMultipartFile("image", imgName, imgContentType, new FileInputStream(imgPath));
        MockMultipartFile docxFile = new MockMultipartFile("file", fileName, fileContentType, new FileInputStream(filePath));
        BoardAcceptanceTestHelper.createBoardWithAll(mvc, token, jsonContent, imgFile, docxFile);

        String title2 = "notice";
        String content2 = "content";
        String memberTier2 = "tier 3";
        BoardUpdateRequest boardUpdateRequest = BoardUpdateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId)
                .memberTier(memberTier2)
                //삭제할 이미지 아이디 List
                //삭제할 파일 아이디 List
                .build();

        String imgName2 = "TestImage2.jpg";
        String imgContentType2 = "jpg";
        String imgPath2 = "./src/test/java/org/waldreg/acceptance/board/TestImage2.jpg";

        String fileName2 = "TestDocx2";
        String fileContentType2 = "docx";
        String filePath2 = "./src/test/java/org/waldreg/acceptance/board/TestDocx2.docx";

        MockPart jsonContent2 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardUpdateRequest).getBytes());
        MockMultipartFile imgFile2 = new MockMultipartFile("image", imgName2, imgContentType2, new FileInputStream(imgPath2));
        MockMultipartFile docxFile2 = new MockMultipartFile("file", fileName2, fileContentType2, new FileInputStream(filePath2));
        //when

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("특정 게시글 삭제 성공")
    public void DELETE_BOARD_SUCCESS_TEST() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "notice";
        String content = "content";
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).memberTier(memberTier1).build();

        String imgName = "TestImage.jpg";
        String imgContentType = "jpg";
        String imgPath = "./src/test/java/org/waldreg/acceptance/board/TestImage.jpg";

        String fileName = "TestDocx";
        String fileContentType = "docx";
        String filePath = "./src/test/java/org/waldreg/acceptance/board/TestDocx.docx";

        MockPart jsonContent = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        MockMultipartFile imgFile = new MockMultipartFile("image", imgName, imgContentType, new FileInputStream(imgPath));
        MockMultipartFile docxFile = new MockMultipartFile("file", fileName, fileContentType, new FileInputStream(filePath));
        BoardAcceptanceTestHelper.createBoardWithAll(mvc, token, jsonContent, imgFile, docxFile);

        //when
        ResultActions boards = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, token);

        BoardListResponse boardListResponse = objectMapper.readValue(boards.andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards()[0].getId();
        ResultActions result = BoardAcceptanceTestHelper.deleteBoard(mvc, token, boardId);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("특정 게시글 강제 삭제 성공")
    public void DELETE_FORCE_BOARD_SUCCESS_TEST() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "notice";
        String content = "content";
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).memberTier(memberTier1).build();

        String imgName = "TestImage.jpg";
        String imgContentType = "jpg";
        String imgPath = "./src/test/java/org/waldreg/acceptance/board/TestImage.jpg";

        String fileName = "TestDocx";
        String fileContentType = "docx";
        String filePath = "./src/test/java/org/waldreg/acceptance/board/TestDocx.docx";

        MockPart jsonContent = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        MockMultipartFile imgFile = new MockMultipartFile("image", imgName, imgContentType, new FileInputStream(imgPath));
        MockMultipartFile docxFile = new MockMultipartFile("file", fileName, fileContentType, new FileInputStream(filePath));
        BoardAcceptanceTestHelper.createBoardWithAll(mvc, token, jsonContent, imgFile, docxFile);

        //when
        ResultActions boards = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, token);

        BoardListResponse boardListResponse = objectMapper.readValue(boards.andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards()[0].getId();
        ResultActions result = BoardAcceptanceTestHelper.deleteBoard(mvc, adminToken, boardId);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("전체 게시글 조회 성공 -json ")
    public void INQUIRY_ALL_BOARD_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title1 = "notice1";
        String content1 = "content";
        int category1 = 1;
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(category1)
                .memberTier(memberTier1)
                .build();
        MockPart jsonContent1 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";
        int category2 = 1;
        String memberTier2 = "tier 3";
        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(category2)
                .memberTier(memberTier2)
                .build();
        MockPart jsonContent2 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notice3";
        String content3 = "content";
        int category3 = 1;
        String memberTier3 = "tier 3";
        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(category3)
                .memberTier(memberTier3)
                .build();
        MockPart jsonContent3 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        //when
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent3);

        ResultActions result = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, token);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(3),
                MockMvcResultMatchers.jsonPath("$.boards.[0].title").value(title1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].content").value(content1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].category").value(category1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].member_tier").value(memberTier1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.user_id").value(userId),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.name").value(name),
                MockMvcResultMatchers.jsonPath("$.boards.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].exist_file").value("false"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.good").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.bad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.check").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.heart").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.smile").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.sad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].title").value(title2),
                MockMvcResultMatchers.jsonPath("$.boards.[2].title").value(title3)
        );
    }

    @Test
    @DisplayName("전체 게시글 조회 성공 -카테고리, json")
    public void INQUIRY_ALL_BOARD_SUCCESS_WITH_CATEGORY_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int category1 = categoryResult.getCategories()[0].getCategoryId();

        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title1 = "notice1";
        String content1 = "content";
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(category1)
                .memberTier(memberTier1)
                .build();
        MockPart jsonContent1 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";
        String memberTier2 = "tier 3";
        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(category1)
                .memberTier(memberTier2)
                .build();
        MockPart jsonContent2 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notice3";
        String content3 = "content";
        int category3 = 3;
        String memberTier3 = "tier 3";
        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(category3)
                .memberTier(memberTier3)
                .build();
        MockPart jsonContent3 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        //when
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent3);

        ResultActions result = BoardAcceptanceTestHelper.inquiryAllBoardWithCategory(mvc, token, category1);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].title").value(title1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].content").value(content1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].category").value(category1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].member_tier").value(memberTier1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.user_id").value(userId),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.name").value(name),
                MockMvcResultMatchers.jsonPath("$.boards.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].exist_file").value("false"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.good").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.bad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.check").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.heart").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.smile").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.sad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].category").value(category1)
        );
    }


    @Test
    @DisplayName("전체 게시글 조회 성공 -json from 1, to 2")
    public void INQUIRY_ALL_BOARD_SUCCESS_WITH_FROM_TO_TEST() throws Exception{
        //given
        int startIdx = 1;
        int endIdx = 2;

        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title1 = "notice1";
        String content1 = "content";
        int category1 = 1;
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(category1)
                .memberTier(memberTier1)
                .build();
        MockPart jsonContent1 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";
        int category2 = 1;
        String memberTier2 = "tier 3";
        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(category2)
                .memberTier(memberTier2)
                .build();
        MockPart jsonContent2 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notice3";
        String content3 = "content";
        int category3 = 3;
        String memberTier3 = "tier 3";
        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(category3)
                .memberTier(memberTier3)
                .build();
        MockPart jsonContent3 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        //when
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent3);

        ResultActions result = BoardAcceptanceTestHelper.inquiryAllBoardWithFromTo(mvc, token, startIdx, endIdx);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].title").value(title2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].content").value(content2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].category").value(category2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].member_tier").value(memberTier2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.user_id").value(userId),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.name").value(name),
                MockMvcResultMatchers.jsonPath("$.boards.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].exist_file").value("false"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.good").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.bad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.check").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.heart").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.smile").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.sad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].category").value(category2)
        );
    }


    @Test
    @DisplayName("전체 게시글 조회 성공 - 카테고리, json, from 1, to 2")
    public void INQUIRY_ALL_BOARD_SUCCESS_WITH_CATEGORY_AND_FROM_TO_TEST() throws Exception{
        //given
        int startIdx = 1;
        int endIdx = 2;
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int category1 = categoryResult.getCategories()[0].getCategoryId();

        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title1 = "notice1";
        String content1 = "content";
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(category1)
                .memberTier(memberTier1)
                .build();
        MockPart jsonContent1 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";
        String memberTier2 = "tier 3";
        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(category1)
                .memberTier(memberTier2)
                .build();
        MockPart jsonContent2 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notice3";
        String content3 = "content";
        int category3 = 3;
        String memberTier3 = "tier 3";
        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(category3)
                .memberTier(memberTier3)
                .build();
        MockPart jsonContent3 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        String title4 = "notice4";
        String content4 = "content";
        String memberTier4 = "tier 3";
        BoardCreateRequest boardCreateRequest4 = BoardCreateRequest.builder()
                .title(title4)
                .content(content4)
                .categoryId(category1)
                .memberTier(memberTier4)
                .build();
        MockPart jsonContent4 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest4).getBytes());

        //when
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent3);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent4);
        ResultActions result = BoardAcceptanceTestHelper.inquiryAllBoardWithCategoryAndFromTo(mvc, token, category1, startIdx, endIdx);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].title").value(title2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].content").value(content2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].category").value(category1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].member_tier").value(memberTier2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.user_id").value(userId),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.name").value(name),
                MockMvcResultMatchers.jsonPath("$.boards.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].exist_file").value("false"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.good").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.bad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.check").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.heart").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.smile").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.sad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].category").value(category1)
        );
    }

    @Test
    @DisplayName("전체 게시글 조회 실패 - 없는 카테고리 아이디")
    public void INQUIRY_ALL_BOARD_Fail_UNKNOWN_CATEGORY_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int category1 = categoryResult.getCategories()[0].getCategoryId();

        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title1 = "notice1";
        String content1 = "content";
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(category1)
                .memberTier(memberTier1)
                .build();
        MockPart jsonContent1 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";
        String memberTier2 = "tier 3";
        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(category1)
                .memberTier(memberTier2)
                .build();
        MockPart jsonContent2 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notice3";
        String content3 = "content";
        String memberTier3 = "tier 3";
        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(category1)
                .memberTier(memberTier3)
                .build();
        MockPart jsonContent3 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        //when

        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent3);
        ResultActions result = BoardAcceptanceTestHelper.inquiryAllBoardWithCategory(mvc, token, -1);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown category id"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("전체 게시글 조회 실패 - 올바르지 않은 범위")
    public void INQUIRY_ALL_BOARD_Fail_INVALID_RANGE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int category1 = categoryResult.getCategories()[0].getCategoryId();

        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title1 = "notice1";
        String content1 = "content";
        String memberTier1 = "tier 3";
        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(category1)
                .memberTier(memberTier1)
                .build();
        MockPart jsonContent1 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";
        String memberTier2 = "tier 3";
        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(category1)
                .memberTier(memberTier2)
                .build();
        MockPart jsonContent2 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notice3";
        String content3 = "content";
        String memberTier3 = "tier 3";
        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(category1)
                .memberTier(memberTier3)
                .build();
        MockPart jsonContent3 = new MockPart("boardCreateRequest", objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        //when

        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, token, jsonContent3);
        ResultActions result = BoardAcceptanceTestHelper.inquiryAllBoardWithFromTo(mvc, token, 1, 5);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid range"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("새로운 카테고리 생성 성공")
    public void CREATE_NEW_CATEGORY_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();

        //when
        ResultActions result = BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("새로운 카테고리 생성 실패 - 권한 없는 사람의 생성 시도 ")
    public void CREATE_NEW_CATEGORY_NO_PERMISSION_TEST() throws Exception{

        //given
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String categoryName = "photo";
        String CategoryMemberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(CategoryMemberTier).build();

        //when
        ResultActions result = BoardAcceptanceTestHelper.createCategory(mvc, token, objectMapper.writeValueAsString(categoryRequest));

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
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();

        //when
        ResultActions result = BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Blank category name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("새로운 카테고리 생성 실패 - 중복된 카테고리 이름")
    public void CREATE_NEW_CATEGORY_DUPLICATED_CATEGORY_TITLE_TEST() throws Exception{

        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();

        //when
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        ResultActions result = BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Duplicated category name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("전체 카테고리 조회 성공")
    public void INQUIRY_ALL_CATEGORY_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName1 = "cate1";
        String memberTier1 = "tier 1";
        CategoryRequest categoryRequest1 = CategoryRequest.builder().categoryName(categoryName1).memberTier(memberTier1).build();

        String categoryName2 = "cate2";
        String memberTier2 = "tier 2";
        CategoryRequest categoryRequest2 = CategoryRequest.builder().categoryName(categoryName2).memberTier(memberTier2).build();

        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest1));
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest2));
        //when
        ResultActions result = BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.categories.[0].category_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.categories.[0].category_name").value(categoryName1),
                MockMvcResultMatchers.jsonPath("$.categories.[0].category_boards").isNumber(),
                MockMvcResultMatchers.jsonPath("$.categories.[0].member_tier").value(memberTier1),
                MockMvcResultMatchers.jsonPath("$.categories.[1].category_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.categories.[1].category_name").value(categoryName2),
                MockMvcResultMatchers.jsonPath("$.categories.[1].category_boards").isNumber(),
                MockMvcResultMatchers.jsonPath("$.categories.[1].member_tier").value(memberTier2)
        );

    }

    @Test
    @DisplayName("카테고리 수정 성공")
    public void MODIFY_CATEGORY_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();

        String modifyName = "modified name";
        CategoryRequest modifyCategoryRequest = CategoryRequest.builder().categoryName(modifyName).memberTier(memberTier).build();
        //when
        ResultActions result = BoardAcceptanceTestHelper.modifyCategory(mvc, adminToken, categoryId, objectMapper.writeValueAsString(modifyCategoryRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("카테고리 수정 실패 - 권한이 없는 경우")
    public void MODIFY_CATEGORY_FAIL_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();

        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String modifyName = "modified name";
        CategoryRequest modifyCategoryRequest = CategoryRequest.builder().categoryName(modifyName).memberTier(memberTier).build();
        //when
        ResultActions result = BoardAcceptanceTestHelper.modifyCategory(mvc, token, categoryId, objectMapper.writeValueAsString(modifyCategoryRequest));

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
    @DisplayName("카테고리 수정 실패 - 카테고리 이름이 공백인 경우")
    public void MODIFY_CATEGORY_FAIL_EMPTY_NAME_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();

        String modifyName = "";
        CategoryRequest modifyCategoryRequest = CategoryRequest.builder().categoryName(modifyName).memberTier(memberTier).build();
        //when
        ResultActions result = BoardAcceptanceTestHelper.modifyCategory(mvc, adminToken, categoryId, objectMapper.writeValueAsString(modifyCategoryRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Blank category name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("카테고리 수정 실패 - 카테고리 이름이 중복인 경우")
    public void MODIFY_CATEGORY_FAIL_DUPLICATED_NAME_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));

        String categoryName2 = "cate2";
        String memberTier2 = "tier 1";
        CategoryRequest categoryRequest2 = CategoryRequest.builder().categoryName(categoryName2).memberTier(memberTier2).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest2));

        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();

        String modifyName = "cate2";
        CategoryRequest modifyCategoryRequest = CategoryRequest.builder().categoryName(modifyName).memberTier(memberTier).build();
        //when
        ResultActions result = BoardAcceptanceTestHelper.modifyCategory(mvc, adminToken, categoryId, objectMapper.writeValueAsString(modifyCategoryRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Duplicated category name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 카테고리 삭제 성공")
    public void DELETE_CATEGORY_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();

        //when
        ResultActions result = BoardAcceptanceTestHelper.deleteCategory(mvc, adminToken, categoryId);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("Api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("특정 카테고리 삭제 실패 - 권한이 없는 유저")
    public void DELETE_CATEGORY_FAIL_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate1";
        String memberTier = "tier 1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).memberTier(memberTier).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();

        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        //when
        ResultActions result = BoardAcceptanceTestHelper.deleteCategory(mvc, token, categoryId);

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
