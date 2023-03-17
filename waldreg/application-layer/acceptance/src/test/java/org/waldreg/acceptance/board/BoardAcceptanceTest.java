package org.waldreg.acceptance.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.waldreg.acceptance.authentication.AuthenticationAcceptanceTestHelper;
import org.waldreg.acceptance.user.UserAcceptanceTestHelper;
import org.waldreg.auth.request.AuthTokenRequest;
import org.waldreg.controller.board.board.request.BoardCreateRequest;
import org.waldreg.controller.board.board.request.BoardUpdateRequest;
import org.waldreg.controller.board.category.request.CategoryRequest;
import org.waldreg.controller.board.board.response.BoardListResponse;
import org.waldreg.controller.board.board.response.BoardResponse;
import org.waldreg.controller.board.category.response.CategoryListResponse;
import org.waldreg.controller.board.category.response.CategoryResponse;
import org.waldreg.controller.board.comment.request.CommentRequest;
import org.waldreg.controller.board.comment.response.CommentListResponse;
import org.waldreg.controller.board.comment.response.CommentResponse;
import org.waldreg.controller.joiningpool.request.UserRequest;
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


    @BeforeEach
    @AfterEach
    public void INITIATE_USER() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        for (UserRequest request : userCreateRequestList){
            UserResponse userResponse = objectMapper.readValue(UserAcceptanceTestHelper.inquiryUserWithoutToken(mvc, request.getUserId())
                                                                       .andReturn()
                                                                       .getResponse()
                                                                       .getContentAsString(), UserResponse.class);
            UserAcceptanceTestHelper.forcedDeleteUserWithToken(mvc, userResponse.getId(), adminToken);
        }
        userCreateRequestList.clear();
    }

    @BeforeEach
    @AfterEach
    public void INITIATE_BOARD() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        for (BoardResponse board : boardListResponse.getBoards()){
            BoardAcceptanceTestHelper.deleteBoard(mvc, adminToken, board.getId()).andExpect(
                    MockMvcResultMatchers.status().isOk()
            );
            BoardAcceptanceTestHelper.inquiryBoardById(mvc, adminToken, board.getId()).andExpectAll(
                    MockMvcResultMatchers.status().isBadRequest(),
                    MockMvcResultMatchers.jsonPath("$.code").value("BOARD-401")
            );
        }
        INITIATE_CATEGORY();
    }

    public void INITIATE_CATEGORY() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        ResultActions result = BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken);
        CategoryResponse[] categoryList = objectMapper.readValue(result.andReturn().getResponse().getContentAsString(), CategoryListResponse.class).getCategories();
        for (CategoryResponse category : categoryList){
            BoardAcceptanceTestHelper.deleteCategory(mvc, adminToken, category.getCategoryId());
        }

        String categoryName = "cate1";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        String categoryName2 = "cate2";
        CategoryRequest categoryRequest2 = CategoryRequest.builder().categoryName(categoryName2).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest2));
    }

    @Test
    @DisplayName("게시글 생성 성공(이미지,파일 포함)")
    public void CREATE_BOARD_WITH_ALL_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate12";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();
        String title = "notice";
        String content = "content";

        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content)
                .categoryId(categoryId)
                .build();

        String imgName = "TestImage.jpg";
        String imgContentType = "image/jpg";
        String imgPath = "./src/test/java/org/waldreg/acceptance/board/TestImage.jpg";

        String fileName = "TestDocx.docx";
        String fileContentType = "application/msword";
        String filePath = "./src/test/java/org/waldreg/acceptance/board/TestDocx.docx";

        MockMultipartFile jsonContent = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        MockMultipartFile imgFile = new MockMultipartFile("image", imgName, imgContentType, new FileInputStream(imgPath));
        MockMultipartFile docxFile = new MockMultipartFile("file", fileName, fileContentType, new FileInputStream(filePath));

        //when
        List<MockMultipartFile> imgFileList = new ArrayList<>();
        List<MockMultipartFile> docxFileList = new ArrayList<>();
        imgFileList.add(imgFile);
        docxFileList.add(docxFile);

        ResultActions result = BoardAcceptanceTestHelper.createBoardWithAll(mvc, adminToken, jsonContent, imgFileList, docxFileList);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("게시글 생성 성공(이미지,파일 여러개)")
    public void CREATE_BOARD_WITH_ALL_FILE_LIST_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String title = "notice";
        String content = "content";

        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content)
                .categoryId(categoryId)
                .build();

        String imgName = "TestImage.jpg";
        String imgContentType = "image/jpg";
        String imgPath = "./src/test/java/org/waldreg/acceptance/board/TestImage.jpg";

        String imgName2 = "TestImage2.jpg";
        String imgContentType2 = "image/jpg";
        String imgPath2 = "./src/test/java/org/waldreg/acceptance/board/TestImage2.jpg";

        String fileName = "TestDocx";
        String fileContentType = "application/msword";
        String filePath = "./src/test/java/org/waldreg/acceptance/board/TestDocx.docx";

        MockMultipartFile jsonContent = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        MockMultipartFile imgFile = new MockMultipartFile("image", imgName, imgContentType, new FileInputStream(imgPath));
        MockMultipartFile imgFile2 = new MockMultipartFile("image", imgName2, imgContentType2, new FileInputStream(imgPath2));
        MockMultipartFile docxFile = new MockMultipartFile("file", fileName, fileContentType, new FileInputStream(filePath));
        //when
        List<MockMultipartFile> imgFileList = new ArrayList<>();
        List<MockMultipartFile> docxFileList = new ArrayList<>();
        imgFileList.add(imgFile);
        imgFileList.add(imgFile2);
        docxFileList.add(docxFile);

        ResultActions result = mvc.perform(MockMvcRequestBuilders.multipart("/board")
                                                   .file(jsonContent)
                                                   .file(imgFileList.get(0))
                                                   .file(imgFileList.get(1))
                                                   .file(docxFileList.get(0))
                                                   .header(HttpHeaders.AUTHORIZATION, adminToken)
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .accept(MediaType.APPLICATION_JSON)
                                                   .header("api-version", apiVersion));
        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("게시글 생성 성공(json 객체만)")
    public void CREATE_BOARD_WITH_ONLY_JSON_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String title = "notice";
        String content = "content";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).build();

        MockMultipartFile jsonContent = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        //when

        ResultActions result = BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("게시글 생성 성공(json과 이미지 포함)")
    public void CREATE_BOARD_WITH_JSON_AND_IMAGE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String title = "notice";
        String content = "content";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).build();

        String imgName = "TestImage.jpg";
        String imgContentType = "image/jpg";
        String imgPath = "./src/test/java/org/waldreg/acceptance/board/TestImage.jpg";

        MockMultipartFile jsonContent = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        MockMultipartFile imgFile = new MockMultipartFile("image", imgName, imgContentType, new FileInputStream(imgPath));
        //when
        List<MockMultipartFile> imgFileList = new ArrayList<>();
        imgFileList.add(imgFile);
        ResultActions result = BoardAcceptanceTestHelper.createBoardWithJsonAndImage(mvc, adminToken, jsonContent, imgFileList);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("게시글 생성 성공(json과 파일 포함)")
    public void CREATE_BOARD_WITH_JSON_AND_FILE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String title = "notice";
        String content = "content";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).build();

        String fileName = "TestDocx.docx";
        String fileContentType = "application/msword";
        String filePath = "./src/test/java/org/waldreg/acceptance/board/TestDocx.docx";

        MockMultipartFile jsonContent = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        MockMultipartFile docxFile = new MockMultipartFile("file", fileName, fileContentType, new FileInputStream(filePath));
        //when
        List<MockMultipartFile> docxFileList = new ArrayList<>();
        docxFileList.add(docxFile);

        ResultActions result = BoardAcceptanceTestHelper.createBoardWithJsonAndFile(mvc, adminToken, jsonContent, docxFileList);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }


    @Test
    @DisplayName("게시글 생성 실패 - 게시글 제목이 빈 제목일 경우")
    public void CREATE_BOARD_FAIL_EMPTY_TITLE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String title = "";
        String content = "content";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).build();

        MockMultipartFile jsonContent = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        //when

        ResultActions result = BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-409"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Blank board title"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    @Test
    @DisplayName("특정 게시글 조회 성공 - json 객체만")
    public void INQUIRY_BOARD_BY_BOARD_ID_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);

        int categoryId = categoryResult.getCategories()[2].getCategoryId();

        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title = "notice";
        String content = "content";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).build();
        MockMultipartFile jsonContent = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest).getBytes());

        //when
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent);
        ResultActions boards = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);

        BoardListResponse boardListResponse = objectMapper.readValue(boards.andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        ResultActions result = BoardAcceptanceTestHelper.inquiryBoardById(mvc, adminToken, boardId);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.id").value(boardId),
                MockMvcResultMatchers.jsonPath("$.title").value(title),
                MockMvcResultMatchers.jsonPath("$.content").value(content),
                MockMvcResultMatchers.jsonPath("$.category").value(categoryName),
                MockMvcResultMatchers.jsonPath("$.author.user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.author.name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.views").value(1),
                MockMvcResultMatchers.jsonPath("$.comment_count").value(0),
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

        String categoryName = "cate3";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
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
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).build();
        MockMultipartFile jsonContent = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest).getBytes());

        //when
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent);
        ResultActions result = BoardAcceptanceTestHelper.inquiryBoardById(mvc, adminToken, -1);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-401"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown board id : -1"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    @Test
    @DisplayName("특정 게시글 수정 성공")
    public void MODIFY_BOARD_WITH_ALL_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        String title2 = "notice";
        String content2 = "content";

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);

        ResultActions resultCategory = BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken);
        CategoryResponse[] categoryList = objectMapper.readValue(resultCategory.andReturn().getResponse().getContentAsString(), CategoryListResponse.class).getCategories();

        String[] fileUrls = boardListResponse.getBoards().get(0).getFiles();

        int boardId = boardListResponse.getBoards().get(0).getId();
        int categoryId = categoryList[0].getCategoryId();
        ArrayList<String> fileUrlList = new ArrayList<>(List.of(fileUrls));

        BoardUpdateRequest boardUpdateRequest = BoardUpdateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId)
                .deleteFileUrls(fileUrlList)
                .build();

        String imgName2 = "TestImage2.jpg";
        String imgContentType2 = "image/jpg";
        String imgPath2 = "./src/test/java/org/waldreg/acceptance/board/TestImage2.jpg";

        String fileName2 = "TestDocx2";
        String fileContentType2 = "application/msword";
        String filePath2 = "./src/test/java/org/waldreg/acceptance/board/TestDocx2.docx";

        MockMultipartFile jsonContent2 = new MockMultipartFile("boardUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardUpdateRequest).getBytes());
        MockMultipartFile imgFile2 = new MockMultipartFile("image", imgName2, imgContentType2, new FileInputStream(imgPath2));
        MockMultipartFile docxFile2 = new MockMultipartFile("file", fileName2, fileContentType2, new FileInputStream(filePath2));
        //when

        List<MockMultipartFile> imgFileList = new ArrayList<>();
        List<MockMultipartFile> docxFileList = new ArrayList<>();
        imgFileList.add(imgFile2);
        docxFileList.add(docxFile2);

        ResultActions result = BoardAcceptanceTestHelper.modifyBoardWithAll(mvc, adminToken, boardId, jsonContent2, imgFileList, docxFileList);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("특정 게시글 수정 성공 - json 본문만 수정")
    public void MODIFY_BOARD_WITH_ONLY_JSON_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        String title2 = "notice";
        String content2 = "content";

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);

        ResultActions resultCategory = BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken);
        CategoryResponse[] categoryList = objectMapper.readValue(resultCategory.andReturn().getResponse().getContentAsString(), CategoryListResponse.class).getCategories();

        String[] fileUrls = boardListResponse.getBoards().get(0).getFiles();

        int boardId = boardListResponse.getBoards().get(0).getId();
        int categoryId = categoryList[0].getCategoryId();
        ArrayList<String> fileUrlList = new ArrayList<>(List.of(fileUrls));

        BoardUpdateRequest boardUpdateRequest = BoardUpdateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId)
                .deleteFileUrls(fileUrlList)
                .build();

        MockMultipartFile jsonContent2 = new MockMultipartFile("boardUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardUpdateRequest).getBytes());
        //when

        ResultActions result = BoardAcceptanceTestHelper.modifyBoardWithOnlyJson(mvc, adminToken, boardId, jsonContent2);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("특정 게시글 수정 성공 - json, 이미지 수정")
    public void MODIFY_BOARD_WITH_JSON_AND_IMAGE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        String title2 = "notice";
        String content2 = "content";

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);

        ResultActions resultCategory = BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken);
        CategoryResponse[] categoryList = objectMapper.readValue(resultCategory.andReturn().getResponse().getContentAsString(), CategoryListResponse.class).getCategories();

        String[] fileUrls = boardListResponse.getBoards().get(0).getFiles();

        int boardId = boardListResponse.getBoards().get(0).getId();
        int categoryId = categoryList[0].getCategoryId();
        ArrayList<String> fileUrlList = new ArrayList<>(List.of(fileUrls));

        BoardUpdateRequest boardUpdateRequest = BoardUpdateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId)
                .deleteFileUrls(fileUrlList)
                .build();

        String imgName2 = "TestImage2.jpg";
        String imgContentType2 = "image/jpg";
        String imgPath2 = "./src/test/java/org/waldreg/acceptance/board/TestImage2.jpg";

        MockMultipartFile jsonContent2 = new MockMultipartFile("boardUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardUpdateRequest).getBytes());
        MockMultipartFile imgFile2 = new MockMultipartFile("image", imgName2, imgContentType2, new FileInputStream(imgPath2));
        //when
        List<MockMultipartFile> imgFileList = new ArrayList<>();
        imgFileList.add(imgFile2);

        ResultActions result = BoardAcceptanceTestHelper.modifyBoardWithJsonAndImage(mvc, adminToken, boardId, jsonContent2, imgFileList);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("특정 게시글 수정 성공 - json, 파일수정")
    public void MODIFY_BOARD_WITH_JSON_AND_FILE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        String title2 = "notice";
        String content2 = "content";

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);

        ResultActions resultCategory = BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken);
        CategoryResponse[] categoryList = objectMapper.readValue(resultCategory.andReturn().getResponse().getContentAsString(), CategoryListResponse.class).getCategories();

        String[] fileUrls = boardListResponse.getBoards().get(0).getFiles();

        int boardId = boardListResponse.getBoards().get(0).getId();
        int categoryId = categoryList[0].getCategoryId();
        ArrayList<String> fileUrlList = new ArrayList<>(List.of(fileUrls));

        BoardUpdateRequest boardUpdateRequest = BoardUpdateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId)
                .deleteFileUrls(fileUrlList)
                .build();

        String fileName2 = "TestDocx2";
        String fileContentType2 = "application/msword";
        String filePath2 = "./src/test/java/org/waldreg/acceptance/board/TestDocx2.docx";

        MockMultipartFile jsonContent2 = new MockMultipartFile("boardUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardUpdateRequest).getBytes());
        MockMultipartFile docxFile2 = new MockMultipartFile("file", fileName2, fileContentType2, new FileInputStream(filePath2));
        //when
        List<MockMultipartFile> docxFileList = new ArrayList<>();
        docxFileList.add(docxFile2);

        ResultActions result = BoardAcceptanceTestHelper.modifyBoardWithJsonAndFile(mvc, adminToken, boardId, jsonContent2, docxFileList);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }


    @Test
    @DisplayName("특정 게시글 수정 실패 - 없는 아이디")
    public void MODIFY_BOARD_FAIL_UNKNOWN_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        String title2 = "notice";
        String content2 = "content";

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);

        ResultActions resultCategory = BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken);
        CategoryResponse[] categoryList = objectMapper.readValue(resultCategory.andReturn().getResponse().getContentAsString(), CategoryListResponse.class).getCategories();

        String[] fileUrls = boardListResponse.getBoards().get(0).getFiles();

        int categoryId = categoryList[0].getCategoryId();
        ArrayList<String> fileUrlList = new ArrayList<>(List.of(fileUrls));

        BoardUpdateRequest boardUpdateRequest = BoardUpdateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId)
                .deleteFileUrls(fileUrlList)
                .build();

        String imgName2 = "TestImage2.jpg";
        String imgContentType2 = "image/jpg";
        String imgPath2 = "./src/test/java/org/waldreg/acceptance/board/TestImage2.jpg";

        String fileName2 = "TestDocx2";
        String fileContentType2 = "application/msword";
        String filePath2 = "./src/test/java/org/waldreg/acceptance/board/TestDocx2.docx";

        MockMultipartFile jsonContent2 = new MockMultipartFile("boardUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardUpdateRequest).getBytes());
        MockMultipartFile imgFile2 = new MockMultipartFile("image", imgName2, imgContentType2, new FileInputStream(imgPath2));
        MockMultipartFile docxFile2 = new MockMultipartFile("file", fileName2, fileContentType2, new FileInputStream(filePath2));
        //when

        List<MockMultipartFile> imgFileList = new ArrayList<>();
        List<MockMultipartFile> docxFileList = new ArrayList<>();
        imgFileList.add(imgFile2);
        docxFileList.add(docxFile2);

        ResultActions result = BoardAcceptanceTestHelper.modifyBoardWithAll(mvc, adminToken, -1, jsonContent2, imgFileList, docxFileList);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-401"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown board id : -1"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    @Test
    @DisplayName("특정 게시글 수정 실패 - 권한이 없는 경우")
    public void MODIFY_BOARD_FAIL_NO_PERMISSION_TEST() throws Exception{
        //given
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        createTier1BoardWithAll();

        String title2 = "notice";
        String content2 = "content";

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, token);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);

        ResultActions resultCategory = BoardAcceptanceTestHelper.inquiryAllCategory(mvc, token);
        CategoryResponse[] categoryList = objectMapper.readValue(resultCategory.andReturn().getResponse().getContentAsString(), CategoryListResponse.class).getCategories();

        String[] fileUrls = boardListResponse.getBoards().get(0).getFiles();

        int boardId = boardListResponse.getBoards().get(0).getId();
        int categoryId = categoryList[0].getCategoryId();
        ArrayList<String> fileUrlList = new ArrayList<>(List.of(fileUrls));

        BoardUpdateRequest boardUpdateRequest = BoardUpdateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId)
                .deleteFileUrls(fileUrlList)
                .build();

        String imgName2 = "TestImage2.jpg";
        String imgContentType2 = "image/jpg";
        String imgPath2 = "./src/test/java/org/waldreg/acceptance/board/TestImage2.jpg";

        String fileName2 = "TestDocx2";
        String fileContentType2 = "application/msword";
        String filePath2 = "./src/test/java/org/waldreg/acceptance/board/TestDocx2.docx";

        MockMultipartFile jsonContent2 = new MockMultipartFile("boardUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardUpdateRequest).getBytes());
        MockMultipartFile imgFile2 = new MockMultipartFile("image", imgName2, imgContentType2, new FileInputStream(imgPath2));
        MockMultipartFile docxFile2 = new MockMultipartFile("file", fileName2, fileContentType2, new FileInputStream(filePath2));
        //when
        List<MockMultipartFile> imgFileList = new ArrayList<>();
        List<MockMultipartFile> docxFileList = new ArrayList<>();
        imgFileList.add(imgFile2);
        docxFileList.add(docxFile2);

        ResultActions result = BoardAcceptanceTestHelper.modifyBoardWithAll(mvc, token, boardId, jsonContent2, imgFileList, docxFileList);

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
    @DisplayName("특정 게시글 삭제 성공")
    public void DELETE_BOARD_SUCCESS_TEST() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String title = "notice";
        String content = "content";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).build();

        String imgName = "TestImage.jpg";
        String imgContentType = "image/jpg";
        String imgPath = "./src/test/java/org/waldreg/acceptance/board/TestImage.jpg";

        String fileName = "TestDocx";
        String fileContentType = "application/msword";
        String filePath = "./src/test/java/org/waldreg/acceptance/board/TestDocx.docx";

        MockMultipartFile jsonContent = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        MockMultipartFile imgFile = new MockMultipartFile("image", imgName, imgContentType, new FileInputStream(imgPath));
        MockMultipartFile docxFile = new MockMultipartFile("file", fileName, fileContentType, new FileInputStream(filePath));
        List<MockMultipartFile> imgFileList = new ArrayList<>();
        List<MockMultipartFile> docxFileList = new ArrayList<>();
        imgFileList.add(imgFile);
        docxFileList.add(docxFile);
        BoardAcceptanceTestHelper.createBoardWithAll(mvc, adminToken, jsonContent, imgFileList, docxFileList);

        //when
        ResultActions boards = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);

        BoardListResponse boardListResponse = objectMapper.readValue(boards.andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        ResultActions result = BoardAcceptanceTestHelper.deleteBoard(mvc, adminToken, boardId);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("특정 게시글 삭제 실패 - 작성자가 아닌 유저")
    public void DELETE_BOARD_FAIL_TEST() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");
        String otherUserToken = createUserAndGetToken("test", "fail", "2gdagdfa@");

        String title = "notice";
        String content = "content";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).build();

        String imgName = "TestImage.jpg";
        String imgContentType = "image/jpg";
        String imgPath = "./src/test/java/org/waldreg/acceptance/board/TestImage.jpg";

        String fileName = "TestDocx.docx";
        String fileContentType = "application/msword";
        String filePath = "./src/test/java/org/waldreg/acceptance/board/TestDocx.docx";

        MockMultipartFile jsonContent = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        MockMultipartFile imgFile = new MockMultipartFile("image", imgName, imgContentType, new FileInputStream(imgPath));
        MockMultipartFile docxFile = new MockMultipartFile("file", fileName, fileContentType, new FileInputStream(filePath));
        List<MockMultipartFile> imgFileList = new ArrayList<>();
        List<MockMultipartFile> docxFileList = new ArrayList<>();
        imgFileList.add(imgFile);
        docxFileList.add(docxFile);
        BoardAcceptanceTestHelper.createBoardWithAll(mvc, adminToken, jsonContent, imgFileList, docxFileList);

        //when
        ResultActions boards = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, token);

        BoardListResponse boardListResponse = objectMapper.readValue(boards.andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        ResultActions result = BoardAcceptanceTestHelper.deleteBoard(mvc, otherUserToken, boardId);

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
    @DisplayName("특정 게시글 강제 삭제 성공")
    public void DELETE_FORCE_BOARD_SUCCESS_TEST() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String title = "notice";
        String content = "content";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).build();

        String imgName = "TestImage.jpg";
        String imgContentType = "image/jpg";
        String imgPath = "./src/test/java/org/waldreg/acceptance/board/TestImage.jpg";

        String fileName = "TestDocx.docx";
        String fileContentType = "application/msword";
        String filePath = "./src/test/java/org/waldreg/acceptance/board/TestDocx.docx";

        MockMultipartFile jsonContent = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        MockMultipartFile imgFile = new MockMultipartFile("image", imgName, imgContentType, new FileInputStream(imgPath));
        MockMultipartFile docxFile = new MockMultipartFile("file", fileName, fileContentType, new FileInputStream(filePath));
        List<MockMultipartFile> imgFileList = new ArrayList<>();
        List<MockMultipartFile> docxFileList = new ArrayList<>();
        imgFileList.add(imgFile);
        docxFileList.add(docxFile);
        BoardAcceptanceTestHelper.createBoardWithAll(mvc, adminToken, jsonContent, imgFileList, docxFileList);

        //when
        ResultActions boards = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);

        BoardListResponse boardListResponse = objectMapper.readValue(boards.andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        ResultActions result = BoardAcceptanceTestHelper.deleteBoard(mvc, adminToken, boardId);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
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

        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();
        int categoryId2 = categoryListResponse.getCategories()[1].getCategoryId();

        String title1 = "notice1";
        String content1 = "content";

        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(categoryId)
                .build();
        MockMultipartFile jsonContent1 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";
        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId2)
                .build();
        MockMultipartFile jsonContent2 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notice3";
        String content3 = "content";
        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(categoryId)
                .build();
        MockMultipartFile jsonContent3 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        //when

        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent3);

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
                MockMvcResultMatchers.jsonPath("$.boards.[0].category").value("cate1"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].views").value(0),
                MockMvcResultMatchers.jsonPath("$.boards.[0].comment_count").value(0),
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

        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();
        int categoryId2 = categoryListResponse.getCategories()[1].getCategoryId();

        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title1 = "notice1";
        String content1 = "content";

        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(categoryId)
                .build();
        MockMultipartFile jsonContent1 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";

        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId)
                .build();
        MockMultipartFile jsonContent2 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notice3";
        String content3 = "content";

        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(categoryId2)
                .build();
        MockMultipartFile jsonContent3 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        //when

        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent3);

        ResultActions result = BoardAcceptanceTestHelper.inquiryAllBoardWithCategory(mvc, token, categoryId);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].title").value(title1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].content").value(content1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].category").value("cate1"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].views").value(0),
                MockMvcResultMatchers.jsonPath("$.boards.[0].comment_count").value(0),
                MockMvcResultMatchers.jsonPath("$.boards.[0].exist_file").value("false"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.good").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.bad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.check").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.heart").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.smile").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.sad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].category").value("cate1")
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

        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, token).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();
        int categoryId2 = categoryListResponse.getCategories()[1].getCategoryId();

        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(categoryId)

                .build();
        MockMultipartFile jsonContent1 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";

        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId)

                .build();
        MockMultipartFile jsonContent2 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        //when
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent2);

        ResultActions result = BoardAcceptanceTestHelper.inquiryAllBoardWithFromTo(mvc, token, startIdx, endIdx);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].title").value(title1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].content").value(content1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].category").value("cate1"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].views").value(0),
                MockMvcResultMatchers.jsonPath("$.boards.[0].comment_count").value(0),
                MockMvcResultMatchers.jsonPath("$.boards.[0].exist_file").value("false"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.good").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.bad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.check").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.heart").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.smile").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.sad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].category").value("cate1")
        );
    }


    @Test
    @DisplayName("전체 게시글 조회 성공 - 카테고리, json, from 1, to 2")
    public void INQUIRY_ALL_BOARD_SUCCESS_WITH_CATEGORY_AND_FROM_TO_TEST() throws Exception{
        //given
        int startIdx = 1;
        int endIdx = 2;
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();
        int categoryId2 = categoryListResponse.getCategories()[1].getCategoryId();

        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title1 = "notice1";
        String content1 = "content";

        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(categoryId)
                .build();
        MockMultipartFile jsonContent1 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";

        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId)

                .build();
        MockMultipartFile jsonContent2 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notice3";
        String content3 = "content";

        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(categoryId2)
                .build();
        MockMultipartFile jsonContent3 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        String title4 = "notice4";
        String content4 = "content";
        BoardCreateRequest boardCreateRequest4 = BoardCreateRequest.builder()
                .title(title4)
                .content(content4)
                .categoryId(categoryId2)
                .build();
        MockMultipartFile jsonContent4 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest4).getBytes());

        //when

        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent3);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent4);
        ResultActions result = BoardAcceptanceTestHelper.inquiryAllBoardWithCategoryAndFromTo(mvc, token, categoryId, startIdx, endIdx);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].title").value(title1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].content").value(content1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].category").value("cate1"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].exist_file").value("false"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.good").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.bad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.check").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.heart").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.smile").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.sad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].category").value("cate1")
        );
    }

    @Test
    @DisplayName("전체 게시글 조회 실패 - 없는 카테고리 아이디")
    public void INQUIRY_ALL_BOARD_Fail_UNKNOWN_CATEGORY_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";

        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
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

        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(category1)

                .build();
        MockMultipartFile jsonContent1 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";

        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(category1)
                .build();
        MockMultipartFile jsonContent2 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notice3";
        String content3 = "content";

        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(category1)

                .build();
        MockMultipartFile jsonContent3 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        //when

        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent3);
        ResultActions result = BoardAcceptanceTestHelper.inquiryAllBoardWithCategory(mvc, token, -1);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-403"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown category id : -1"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("전체 게시글 조회 실패 - 올바르지 않은 범위")
    public void INQUIRY_ALL_BOARD_FAIL_INVALID_RANGE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";

        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
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

        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(category1)

                .build();
        MockMultipartFile jsonContent1 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";

        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(category1)
                .build();
        MockMultipartFile jsonContent2 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notice3";
        String content3 = "content";

        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(category1)
                .build();
        MockMultipartFile jsonContent3 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        //when
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent3);
        ResultActions result = BoardAcceptanceTestHelper.inquiryAllBoardWithFromTo(mvc, token, -1, 0);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-404"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid range from : -1 to : 0"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("게시글 검색 성공 - 제목으로 검색, 1~2개 ")
    public void SEARCH_BOARD_BY_TITLE_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title1 = "notice1";
        String content1 = "content";

        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, token).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();
        int categoryId2 = categoryListResponse.getCategories()[1].getCategoryId();

        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(categoryId)
                .build();
        MockMultipartFile jsonContent1 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";

        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId2)
                .build();
        MockMultipartFile jsonContent2 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notic3";
        String content3 = "content";

        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(categoryId)
                .build();
        MockMultipartFile jsonContent3 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        //when
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent3);

        ResultActions result = BoardAcceptanceTestHelper.searchBoard(mvc, token, "title", "notice", 1, 2);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].title").value(title1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].content").value(content1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].category").value("cate1"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].views").value(0),
                MockMvcResultMatchers.jsonPath("$.boards.[0].comment_count").value(0),
                MockMvcResultMatchers.jsonPath("$.boards.[0].exist_file").value("false"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.good").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.bad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.check").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.heart").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.smile").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.sad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].title").value(title2),
                MockMvcResultMatchers.jsonPath("$.boards.[1].content").value(content2),
                MockMvcResultMatchers.jsonPath("$.boards.[1].category").value("cate2"),
                MockMvcResultMatchers.jsonPath("$.boards.[1].author.user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[1].author.name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[1].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].views").value(0),
                MockMvcResultMatchers.jsonPath("$.boards.[1].comment_count").value(0),
                MockMvcResultMatchers.jsonPath("$.boards.[1].exist_file").value("false"),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.good").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.bad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.check").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.heart").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.smile").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.sad").isNumber()
        );
    }

    @Test
    @DisplayName("게시글 검색 성공 - 내용으로 검색, 1~2개 ")
    public void SEARCH_BOARD_BY_content_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String title1 = "notice1";
        String content1 = "content";
        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, token).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();

        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(categoryId)
                .build();
        MockMultipartFile jsonContent1 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content2";

        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId)
                .build();
        MockMultipartFile jsonContent2 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notice3";
        String content3 = "conten3";

        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(categoryId)
                .build();
        MockMultipartFile jsonContent3 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        //when
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent3);

        ResultActions result = BoardAcceptanceTestHelper.searchBoard(mvc, adminToken, "content", "content", 1, 2);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].title").value(title1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].content").value(content1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].category").value("cate1"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.name").value("Admin"),
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
                MockMvcResultMatchers.jsonPath("$.boards.[1].content").value(content2),
                MockMvcResultMatchers.jsonPath("$.boards.[1].category").value("cate1"),
                MockMvcResultMatchers.jsonPath("$.boards.[1].author.user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[1].author.name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[1].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].exist_file").value("false"),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.good").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.bad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.check").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.heart").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.smile").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.sad").isNumber()
        );
    }

    @Test
    @DisplayName("게시글 검색 성공 - 작성자로 검색, 1~2개 ")
    public void SEARCH_BOARD_BY_AUTHOR_SUCCESS_TEST() throws Exception{
        //given
        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title1 = "notice1";
        String content1 = "content";
        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, token).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();

        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(categoryId)
                .build();
        MockMultipartFile jsonContent1 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";

        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId)
                .build();
        MockMultipartFile jsonContent2 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        //when
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent2);

        ResultActions result = BoardAcceptanceTestHelper.searchBoard(mvc, token, "author", "Admin", 1, 2);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(2),
                MockMvcResultMatchers.jsonPath("$.boards.[0].title").value(title1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].content").value(content1),
                MockMvcResultMatchers.jsonPath("$.boards.[0].category").value("cate1"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].author.name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].views").value(0),
                MockMvcResultMatchers.jsonPath("$.boards.[0].comment_count").value(0),
                MockMvcResultMatchers.jsonPath("$.boards.[0].exist_file").value("false"),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.good").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.bad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.check").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.heart").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.smile").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[0].reactions.sad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].title").value(title2),
                MockMvcResultMatchers.jsonPath("$.boards.[1].content").value(content2),
                MockMvcResultMatchers.jsonPath("$.boards.[1].category").value("cate1"),
                MockMvcResultMatchers.jsonPath("$.boards.[1].author.user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[1].author.name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.boards.[1].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].views").value(0),
                MockMvcResultMatchers.jsonPath("$.boards.[1].comment_count").value(0),
                MockMvcResultMatchers.jsonPath("$.boards.[1].exist_file").value("false"),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.good").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.bad").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.check").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.heart").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.smile").isNumber(),
                MockMvcResultMatchers.jsonPath("$.boards.[1].reactions.sad").isNumber()
        );
    }

    @Test
    @DisplayName("게시글 검색 실패 - 검색하려는 타입이 잘못되었을때")
    public void SEARCH_BOARD_BY_UNKNOWN_TYPE_TEST() throws Exception{
        //given
        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title1 = "notice1";
        String content1 = "content";
        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, token).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();

        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(categoryId)
                .build();
        MockMultipartFile jsonContent1 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";

        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(categoryId)
                .build();

        MockMultipartFile jsonContent2 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notice3";
        String content3 = "content";

        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(categoryId)

                .build();
        MockMultipartFile jsonContent3 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        //when
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent3);

        ResultActions result = BoardAcceptanceTestHelper.searchBoard(mvc, token, "unknown", "notice", 0, 1);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-407"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown type"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("게시글 검색 실패 - 검색하려는 범위가 잘못되었을때")
    public void SEARCH_BOARD_BY_INVALID_RANGE_TEST() throws Exception{
        //given
        String name = "alcuk";
        String userId = "alcuk_id";
        String userPassword = "2gdddddd!";
        String token = createUserAndGetToken(name, userId, userPassword);

        String title1 = "notice1";
        String content1 = "content";
        int category1 = 1;

        BoardCreateRequest boardCreateRequest1 = BoardCreateRequest.builder()
                .title(title1)
                .content(content1)
                .categoryId(category1)

                .build();
        MockMultipartFile jsonContent1 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest1).getBytes());

        String title2 = "notice2";
        String content2 = "content";
        int category2 = 1;

        BoardCreateRequest boardCreateRequest2 = BoardCreateRequest.builder()
                .title(title2)
                .content(content2)
                .categoryId(category2)

                .build();
        MockMultipartFile jsonContent2 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest2).getBytes());

        String title3 = "notice3";
        String content3 = "content";
        int category3 = 1;

        BoardCreateRequest boardCreateRequest3 = BoardCreateRequest.builder()
                .title(title3)
                .content(content3)
                .categoryId(category3)

                .build();
        MockMultipartFile jsonContent3 = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest3).getBytes());

        //when
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent1);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent2);
        BoardAcceptanceTestHelper.createBoardWithOnlyJson(mvc, adminToken, jsonContent3);

        ResultActions result = BoardAcceptanceTestHelper.searchBoard(mvc, token, "title", "notice", -1, -2);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-404"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid range from : -1 to : -2"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }


    @Test
    @DisplayName("특정 게시글에 댓글 작성 성공")
    public void CREATE_COMMENT_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        CommentRequest commentRequest1 = CommentRequest.builder()
                .content("comment1")
                .build();
        String content = objectMapper.writeValueAsString(commentRequest1);

        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content);
        //when
        ResultActions result = BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("특정 게시글에 댓글 작성 실패 - 권한이 없는 경우")
    public void CREATE_COMMENT_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        CommentRequest commentRequest1 = CommentRequest.builder()
                .content("comment1").build();
        String content = objectMapper.writeValueAsString(commentRequest1);

        //when
        ResultActions result = BoardAcceptanceTestHelper.createComment(mvc, token, boardId, content);

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
    @DisplayName("특정 게시글에 댓글 작성 실패 - 내용이 1000자를 넘어가는 경우")
    public void CREATE_COMMENT_OVER_FLOW_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        String overFlowContent = "comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1";
        CommentRequest commentRequest1 = CommentRequest.builder()
                .content(overFlowContent).build();
        String content = objectMapper.writeValueAsString(commentRequest1);
        //when
        ResultActions result = BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-405"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Overflow comment content"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }


    @Test
    @DisplayName("특정 게시글의 댓글 조회 성공 - 0~1 인덱스 댓글 조회")
    public void INQUIRY_COMMENT_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        CommentRequest commentRequest1 = CommentRequest.builder()
                .content("comment1").build();
        CommentRequest commentRequest2 = CommentRequest.builder()
                .content("comment2").build();
        String content = objectMapper.writeValueAsString(commentRequest1);
        String content2 = objectMapper.writeValueAsString(commentRequest2);

        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content);
        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content2);

        //when
        ResultActions result = BoardAcceptanceTestHelper.inquiryComment(mvc, adminToken, boardId, 1, 3);
        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.max_idx").value(2),
                MockMvcResultMatchers.jsonPath("$.comments.[0].user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.comments.[0].name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.comments.[0].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.comments.[0].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.comments.[0].content").value("comment1"),
                MockMvcResultMatchers.jsonPath("$.comments.[1].user_id").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.comments.[1].name").value("Admin"),
                MockMvcResultMatchers.jsonPath("$.comments.[1].created_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.comments.[1].last_modified_at").isNotEmpty(),
                MockMvcResultMatchers.jsonPath("$.comments.[1].content").value("comment2")
        );

    }

    @Test
    @DisplayName("특정 게시글의 댓글 조회 실패 - 없는 게시글 아이디")
    public void INQUIRY_COMMENT_FAIL_UNKNOWN_BOARD_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        CommentRequest commentRequest1 = CommentRequest.builder()
                .content("comment1").build();
        CommentRequest commentRequest2 = CommentRequest.builder()
                .content("comment2").build();
        String content = objectMapper.writeValueAsString(commentRequest1);
        String content2 = objectMapper.writeValueAsString(commentRequest2);
        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content);
        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content2);

        //when
        ResultActions result = BoardAcceptanceTestHelper.inquiryComment(mvc, adminToken, -1, 0, 1);
        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-401"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown board id : -1"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    @Test
    @DisplayName("특정 게시글의 댓글 조회 실패 - 권한이 없는 경우")
    public void INQUIRY_COMMENT_FAIL_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        CommentRequest commentRequest1 = CommentRequest.builder()
                .content("comment1").build();
        CommentRequest commentRequest2 = CommentRequest.builder()
                .content("comment2").build();
        String content = objectMapper.writeValueAsString(commentRequest1);
        String content2 = objectMapper.writeValueAsString(commentRequest2);
        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content);
        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content2);

        //when
        ResultActions result = BoardAcceptanceTestHelper.inquiryComment(mvc, token, 1, 0, 1);
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
    @DisplayName("특정 게시글의 댓글 조회 실패 - 잘못된 범위")
    public void INQUIRY_COMMENT_FAIL_INVALID_RANGE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        CommentRequest commentRequest1 = CommentRequest.builder()
                .content("comment1").build();
        CommentRequest commentRequest2 = CommentRequest.builder()
                .content("comment2").build();
        String content = objectMapper.writeValueAsString(commentRequest1);
        String content2 = objectMapper.writeValueAsString(commentRequest2);

        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content);
        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content2);

        //when
        ResultActions result = BoardAcceptanceTestHelper.inquiryComment(mvc, adminToken, boardId, -1, -2);
        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-404"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid range from : -1 to : -2"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    @Test
    @DisplayName("특정 게시글의 댓글 수정 성공 ")
    public void MODIFY_COMMENT_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        CommentRequest commentRequest1 = CommentRequest.builder()
                .content("comment1").build();
        String content = objectMapper.writeValueAsString(commentRequest1);
        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content);

        ResultActions comments = BoardAcceptanceTestHelper.inquiryComment(mvc, adminToken, boardId, 1, 2);
        CommentResponse comment1 = objectMapper.readValue(comments.andReturn().getResponse().getContentAsString(), CommentListResponse.class).getCommentResponseList().get(0);

        int commentId = comment1.getId();
        String modifyContent = "modify Content";
        CommentRequest commentRequest2 = CommentRequest.builder()
                .content(modifyContent).build();
        String content2 = objectMapper.writeValueAsString(commentRequest2);

        //when
        ResultActions result = BoardAcceptanceTestHelper.modifyComment(mvc, adminToken, commentId, content2);
        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("특정 게시글의 댓글 수정 실패 - 1000자를 넘는 경우")
    public void MODIFY_COMMENT_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        CommentRequest commentRequest1 = CommentRequest.builder()
                .content("comment1").build();
        String content = objectMapper.writeValueAsString(commentRequest1);
        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content);

        ResultActions comments = BoardAcceptanceTestHelper.inquiryComment(mvc, adminToken, boardId, 1, 2);
        CommentResponse comment1 = objectMapper.readValue(comments.andReturn().getResponse().getContentAsString(), CommentListResponse.class).getCommentResponseList().get(0);

        int commentId = comment1.getId();
        String modifyContent = "comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1comment1";
        CommentRequest commentRequest2 = CommentRequest.builder()
                .content(modifyContent).build();
        String content2 = objectMapper.writeValueAsString(commentRequest2);

        //when
        ResultActions result = BoardAcceptanceTestHelper.modifyComment(mvc, adminToken, commentId, content2);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-405"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Overflow comment content"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    @Test
    @DisplayName("특정 게시글의 댓글 수정 실패 - 댓글 아이디가 없는 경우 ")
    public void MODIFY_COMMENT_UNKNOWN_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        CommentRequest commentRequest1 = CommentRequest.builder()
                .content("comment1").build();
        String content = objectMapper.writeValueAsString(commentRequest1);
        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content);

        String modifyContent = "modify content";
        CommentRequest commentRequest2 = CommentRequest.builder()
                .content(modifyContent).build();
        String content2 = objectMapper.writeValueAsString(commentRequest2);

        //when
        ResultActions result = BoardAcceptanceTestHelper.modifyComment(mvc, adminToken, -1, content2);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-406"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown comment id : -1"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    @Test
    @DisplayName("특정 게시글의 댓글 삭제 성공")
    public void DELETE_COMMENT_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        CommentRequest commentRequest1 = CommentRequest.builder()
                .content("comment1").build();
        String content = objectMapper.writeValueAsString(commentRequest1);
        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content);

        ResultActions comments = BoardAcceptanceTestHelper.inquiryComment(mvc, adminToken, boardId, 1, 2);
        CommentResponse comment1 = objectMapper.readValue(comments.andReturn().getResponse().getContentAsString(), CommentListResponse.class).getCommentResponseList().get(0);

        int commentId = comment1.getId();
        //when
        ResultActions result = BoardAcceptanceTestHelper.deleteComment(mvc, adminToken, commentId);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("특정 게시글의 댓글 삭제 실패 - 권한 없을때")
    public void DELETE_COMMENT_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        CommentRequest commentRequest1 = CommentRequest.builder()
                .content("comment1").build();
        String content = objectMapper.writeValueAsString(commentRequest1);
        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content);

        ResultActions comments = BoardAcceptanceTestHelper.inquiryComment(mvc, adminToken, boardId, 1, 2);
        CommentResponse comment1 = objectMapper.readValue(comments.andReturn().getResponse().getContentAsString(), CommentListResponse.class).getCommentResponseList().get(0);

        int commentId = comment1.getId();
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");
        //when
        ResultActions result = BoardAcceptanceTestHelper.deleteComment(mvc, token, commentId);

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
    @DisplayName("특정 게시글의 댓글 삭제 실패 - 없는 댓글 아이디")
    public void DELETE_COMMENT_UNKNOWN_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        CommentRequest commentRequest1 = CommentRequest.builder()
                .content("comment1").build();
        String content = objectMapper.writeValueAsString(commentRequest1);
        BoardAcceptanceTestHelper.createComment(mvc, adminToken, boardId, content);

        //when
        ResultActions result = BoardAcceptanceTestHelper.deleteComment(mvc, adminToken, -1);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-406"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown comment id : -1"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    @Test
    @DisplayName("특정 게시글의 리액션 성공")
    public void REACTION_CREATE_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        String reactionType = "GOOD";
        //when
        ResultActions result = BoardAcceptanceTestHelper.createReaction(mvc, adminToken, boardId, reactionType);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("특정 게시글의 리액션 취소 성공")
    public void REACTION_CANCEL_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        String reactionType = "GOOD";
        //when
        BoardAcceptanceTestHelper.createReaction(mvc, adminToken, boardId, reactionType);
        BoardAcceptanceTestHelper.createReaction(mvc, adminToken, boardId, reactionType);
        ResultActions result = BoardAcceptanceTestHelper.inquiryBoardById(mvc, adminToken, boardId);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.reactions.good").value(0)
        );

    }

    @Test
    @DisplayName("특정 게시글의 리액션 수정 성공")
    public void REACTION_MODIFY_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();
        String reactionType = "GOOD";
        String modifyReactionType = "BAD";
        //when
        BoardAcceptanceTestHelper.createReaction(mvc, adminToken, boardId, reactionType);
        BoardAcceptanceTestHelper.createReaction(mvc, adminToken, boardId, modifyReactionType);

        ResultActions result = BoardAcceptanceTestHelper.inquiryBoardById(mvc, adminToken, boardId);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.jsonPath("$.reactions.good").value(0),
                MockMvcResultMatchers.jsonPath("$.reactions.bad").value(1)
        );
    }

    @Test
    @DisplayName("특정 게시글의 리액션 실패 - 없는 게시글 아이디")
    public void REACTION_CREATE_FAIL_UNKNOWN_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();
        String reactionType = "GOOD";
        //when
        ResultActions result = BoardAcceptanceTestHelper.createReaction(mvc, adminToken, -1, reactionType);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-401"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown board id : -1"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    @Test
    @DisplayName("특정 게시글의 리액션 실패 - 없는 반응 타입")
    public void REACTION_CREATE_FAIL_UNKNOWN_REACTION_TYPE_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();

        String reactionType = "Invalid reaction type";
        //when
        ResultActions result = BoardAcceptanceTestHelper.createReaction(mvc, adminToken, boardId, reactionType);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-402"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Invalid reaction type reactionType: Invalid reaction type"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    @Test
    @DisplayName("특정 게시글의 리액션 실패 - 권한이 없는 경우")
    public void REACTION_CREATE_FAIL_NO_PERMISSION_TEST() throws Exception{
        //given
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        createTier1BoardWithAll();

        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        int boardId = boardListResponse.getBoards().get(0).getId();

        String reactionType = "GOOD";
        //when
        ResultActions result = BoardAcceptanceTestHelper.createReaction(mvc, token, boardId, reactionType);

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
    @DisplayName("새로운 카테고리 생성 성공")
    public void CREATE_NEW_CATEGORY_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";

        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();

        //when
        ResultActions result = BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("새로운 카테고리 생성 실패 - 권한 없는 사람의 생성 시도 ")
    public void CREATE_NEW_CATEGORY_NO_PERMISSION_TEST() throws Exception{

        //given
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String categoryName = "photo";
        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();

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

        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();

        //when
        ResultActions result = BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-411"),
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

        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();

        //when
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        ResultActions result = BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-412"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Duplicated category name : cate1"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("전체 카테고리 조회 성공")
    public void INQUIRY_ALL_CATEGORY_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName1 = "cate3";
        CategoryRequest categoryRequest1 = CategoryRequest.builder().categoryName(categoryName1).build();

        String categoryName2 = "cate2";
        CategoryRequest categoryRequest2 = CategoryRequest.builder().categoryName(categoryName2).build();

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
                MockMvcResultMatchers.jsonPath("$.categories.[0].category_name").value("cate1"),
                MockMvcResultMatchers.jsonPath("$.categories.[0].category_boards").value(0),
                MockMvcResultMatchers.jsonPath("$.categories.[1].category_id").isNumber(),
                MockMvcResultMatchers.jsonPath("$.categories.[1].category_name").value("cate2"),
                MockMvcResultMatchers.jsonPath("$.categories.[1].category_boards").value(0)
        );

    }

    @Test
    @DisplayName("카테고리 수정 성공")
    public void MODIFY_CATEGORY_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";

        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();

        String modifyName = "modified name";
        CategoryRequest modifyCategoryRequest = CategoryRequest.builder().categoryName(modifyName).build();
        //when
        ResultActions result = BoardAcceptanceTestHelper.modifyCategory(mvc, adminToken, categoryId, objectMapper.writeValueAsString(modifyCategoryRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );
    }

    @Test
    @DisplayName("카테고리 수정 실패 - 권한이 없는 경우")
    public void MODIFY_CATEGORY_FAIL_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";

        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();

        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        String modifyName = "modified name";
        CategoryRequest modifyCategoryRequest = CategoryRequest.builder().categoryName(modifyName).build();
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

        String categoryName = "cate3";

        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();

        String modifyName = "";
        CategoryRequest modifyCategoryRequest = CategoryRequest.builder().categoryName(modifyName).build();
        //when
        ResultActions result = BoardAcceptanceTestHelper.modifyCategory(mvc, adminToken, categoryId, objectMapper.writeValueAsString(modifyCategoryRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-411"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Blank category name"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("카테고리 수정 실패 - 카테고리 이름이 중복인 경우")
    public void MODIFY_CATEGORY_FAIL_DUPLICATED_NAME_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";

        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));

        String categoryName2 = "cate2";
        CategoryRequest categoryRequest2 = CategoryRequest.builder().categoryName(categoryName2).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest2));

        CategoryListResponse categoryListResponse = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken).andReturn().getResponse().getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryListResponse.getCategories()[0].getCategoryId();

        String modifyName = "cate2";
        CategoryRequest modifyCategoryRequest = CategoryRequest.builder().categoryName(modifyName).build();
        //when
        ResultActions result = BoardAcceptanceTestHelper.modifyCategory(mvc, adminToken, categoryId, objectMapper.writeValueAsString(modifyCategoryRequest));

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-412"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Duplicated category name : cate2"),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );
    }

    @Test
    @DisplayName("특정 카테고리 삭제 성공")
    public void DELETE_CATEGORY_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";

        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
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

        String categoryName = "cate3";

        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
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

    @Test
    @DisplayName("이미지 불러오기 테스트")
    public void IMAGE_LOAD_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        String[] imagesUrls = boardListResponse.getBoards().get(0).getImages();

        //when
        ResultActions result = BoardAcceptanceTestHelper.getImage(mvc, adminToken, "/image/" + imagesUrls[0]);
        //then

        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }

    @Test
    @DisplayName("이미지 불러오기 테스트 실패 - 없는 이미지 아이디")
    public void IMAGE_LOAD_FAIL_UNKNOWN_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        String failImageRequestUrl = "/test.png";

        //when
        ResultActions result = BoardAcceptanceTestHelper.getImage(mvc, adminToken, "/image/" + failImageRequestUrl);
        //then

        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.code").value("BOARD-408"),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown file id \"test.png\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    @Test
    @DisplayName("이미지 불러오기 테스트 실패 - 권한이 없는 경우")
    public void IMAGE_LOAD_FAIL_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        String[] imagesUrls = boardListResponse.getBoards().get(0).getImages();

        //when
        ResultActions result = BoardAcceptanceTestHelper.getImage(mvc, token, "/image/" + imagesUrls[0]);
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
    @DisplayName("파일 다운로드 테스트 성공")
    public void FILE_DOWNLOAD_SUCCESS_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        String[] fileUrls = boardListResponse.getBoards().get(0).getFiles();
        //when
        ResultActions result = BoardAcceptanceTestHelper.downloadFile(mvc, adminToken, "/file/" + fileUrls[0]);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.header().string("api-version", apiVersion)
        );

    }


    @Test
    @DisplayName("파일 다운로드 테스트 실패 - 없는 파일 아이디")
    public void FILE_DOWNLOAD_FAIL_UNKNOWN_ID_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        createTier1BoardWithAll();

        String failFileRequestUrl = "test.msword";

        //when
        ResultActions result = BoardAcceptanceTestHelper.downloadFile(mvc, adminToken, "/file/" + failFileRequestUrl);

        //then
        result.andExpectAll(
                MockMvcResultMatchers.status().isBadRequest(),
                MockMvcResultMatchers.header().string(HttpHeaders.CONTENT_TYPE, "application/json"),
                MockMvcResultMatchers.header().string("api-version", apiVersion),
                MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                MockMvcResultMatchers.jsonPath("$.messages").value("Unknown file id \"test.msword\""),
                MockMvcResultMatchers.jsonPath("$.document_url").value("docs.waldreg.org")
        );

    }

    @Test
    @DisplayName("파일 다운로드 테스트 실패 - 권한이 없는 경우")
    public void FILE_DOWNLOAD_FAIL_NO_PERMISSION_TEST() throws Exception{
        //given
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);
        String token = createUserAndGetToken("alcuk", "alcuk_id", "2gdddddd!");

        createTier1BoardWithAll();

        ResultActions resultActions = BoardAcceptanceTestHelper.inquiryAllBoard(mvc, adminToken);
        BoardListResponse boardListResponse = objectMapper.readValue(resultActions.andReturn().getResponse().getContentAsString(), BoardListResponse.class);
        String[] fileUrls = boardListResponse.getBoards().get(0).getFiles();

        //when
        ResultActions result = BoardAcceptanceTestHelper.downloadFile(mvc, token, "/file/" + fileUrls[0]);

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
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        UserRequest userRequest = UserRequest.builder()
                .name(name)
                .userId(userId)
                .userPassword(userPassword)
                .phoneNumber("010-1234-1234")
                .build();
        UserAcceptanceTestHelper.createUser(mvc, objectMapper.writeValueAsString(userRequest));
        UserAcceptanceTestHelper.approveJoinRequest(mvc,adminToken,userRequest.getUserId());
        userCreateRequestList.add(userRequest);

        return AuthenticationAcceptanceTestHelper.getToken(mvc, objectMapper, AuthTokenRequest.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build());
    }

    private void createTier1BoardWithAll() throws Exception{
        String adminToken = AuthenticationAcceptanceTestHelper.getAdminToken(mvc, objectMapper);

        String categoryName = "cate3";

        CategoryRequest categoryRequest = CategoryRequest.builder().categoryName(categoryName).build();
        BoardAcceptanceTestHelper.createCategory(mvc, adminToken, objectMapper.writeValueAsString(categoryRequest));
        CategoryListResponse categoryResult = objectMapper.readValue(BoardAcceptanceTestHelper.inquiryAllCategory(mvc, adminToken)
                                                                             .andReturn()
                                                                             .getResponse()
                                                                             .getContentAsString(), CategoryListResponse.class);
        int categoryId = categoryResult.getCategories()[0].getCategoryId();

        String title = "notice";
        String content = "content";
        BoardCreateRequest boardCreateRequest = BoardCreateRequest.builder()
                .title(title)
                .content(content).categoryId(categoryId).build();

        String imgName = "TestImage.jpg";
        String imgContentType = "image/jpg";
        String imgPath = "./src/test/java/org/waldreg/acceptance/board/TestImage.jpg";

        String fileName = "TestDocx";
        String fileContentType = "application/msword";
        String filePath = "./src/test/java/org/waldreg/acceptance/board/TestDocx.docx";

        MockMultipartFile jsonContent = new MockMultipartFile("boardCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsString(boardCreateRequest).getBytes());
        MockMultipartFile imgFile = new MockMultipartFile("image", imgName, imgContentType, new FileInputStream(imgPath));
        MockMultipartFile docxFile = new MockMultipartFile("file", fileName, fileContentType, new FileInputStream(filePath));
        List<MockMultipartFile> imgFileList = new ArrayList<>();
        List<MockMultipartFile> docxFileList = new ArrayList<>();
        imgFileList.add(imgFile);
        docxFileList.add(docxFile);
        BoardAcceptanceTestHelper.createBoardWithAll(mvc, adminToken, jsonContent, imgFileList, docxFileList);

    }

}
