package org.waldreg.board.board.management;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.board.dto.BoardServiceReactionType;
import org.waldreg.board.exception.BoardDoesNotExistException;
import org.waldreg.board.exception.CategoryDoesNotExistException;
import org.waldreg.board.exception.FileDoesNotSavedException;
import org.waldreg.board.exception.InvalidRangeException;
import org.waldreg.board.board.file.FileInfoGettable;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.board.spi.BoardInCategoryRepository;
import org.waldreg.board.board.spi.BoardUserRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.util.token.DecryptedTokenContextGetter;

@Service
public class DefaultBoardManager implements BoardManager{

    private final int perPage = 20;
    private final BoardRepository boardRepository;
    private final BoardUserRepository boardUserRepository;
    private final BoardInCategoryRepository boardInCategoryRepository;
    private final DecryptedTokenContextGetter decryptedTokenContextGetter;
    private final FileInfoGettable fileInfoGettable;

    @Autowired
    public DefaultBoardManager(BoardRepository boardRepository, BoardInCategoryRepository boardInCategoryRepository, BoardUserRepository boardUserRepository, DecryptedTokenContextGetter decryptedTokenContextGetter, FileInfoGettable fileInfoGettable){
        this.boardRepository = boardRepository;
        this.boardInCategoryRepository = boardInCategoryRepository;
        this.boardUserRepository = boardUserRepository;
        this.decryptedTokenContextGetter = decryptedTokenContextGetter;
        this.fileInfoGettable = fileInfoGettable;
    }

    @Override
    public void createBoard(BoardRequest request){
        throwIfCategoryDoesNotExist(request.getCategoryId());
        List<String> saveFileNameList = getFileNameList();
        List<String> saveImageNameList = getImageNameList();
        BoardDto boardDto = buildBoardDto(request);
        boardDto.setFileUrls(saveFileNameList);
        boardDto.setImageUrls(saveImageNameList);

        boardDto = boardRepository.createBoard(boardDto);
        boardInCategoryRepository.addBoardInCategoryBoardList(boardDto);
    }

    private void throwIfCategoryDoesNotExist(int categoryId){
        if (!boardInCategoryRepository.isExistCategory(categoryId)){
            throw new CategoryDoesNotExistException("BOARD-403","Unknown category id : " + categoryId);
        }
    }

    private List<String> getFileNameList(){
        List<String> fileNameList = new ArrayList<>();
        for (Future<String> future : fileInfoGettable.getSavedFileNameList()){
            String fileName = throwIfFileDoesNotSaved(future);
            fileNameList.add(fileName);
        }
        return fileNameList;
    }

    private List<String> getImageNameList(){
        List<String> fileNameList = new ArrayList<>();
        for (Future<String> future : fileInfoGettable.getSavedImageNameList()){
            String fileName = throwIfFileDoesNotSaved(future);
            fileNameList.add(fileName);
        }
        return fileNameList;
    }

    private String throwIfFileDoesNotSaved(Future<String> future){
        try{
            return future.get();
        } catch (InterruptedException | ExecutionException e){
            throw new FileDoesNotSavedException("BOARD-502","File does not saved");
        }
    }

    private BoardDto buildBoardDto(BoardRequest request){
        UserDto userDto = boardUserRepository.getUserInfo(request.getAuthorId());
        return BoardDto.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .categoryId(request.getCategoryId())
                .userDto(userDto)
                .build();
    }

    @Override
    public BoardDto inquiryBoardById(int id){
        throwIfBoardDoesNotExist(id);
        BoardDto boardDto = boardRepository.inquiryBoardById(id);
        CategoryDto categoryDto = boardInCategoryRepository.inquiryCategoryById(boardDto.getCategoryId());
        boardDto.setCategoryName(categoryDto.getCategoryName());
        boardDto.setViews(boardDto.getViews() + 1);
        boardRepository.modifyBoard(boardDto);
        return boardDto;
    }

    private void throwIfBoardDoesNotExist(int boardId){
        if (!boardRepository.isExistBoard(boardId)){
            throw new BoardDoesNotExistException("BOARD-401","Unknown board id : " + boardId);
        }
    }

    @Override
    public List<BoardDto> inquiryAllBoard(int from, int to){
        throwIfInvalidRangeDetected(from, to);
        int maxIdx = boardRepository.getBoardMaxIdx();
        to = adjustEndIdx(from, to, maxIdx);
        List<BoardDto> boardDtoList = boardRepository.inquiryAllBoard(from, to);
        return setCategoryName(boardDtoList);
    }

    private void throwIfInvalidRangeDetected(int from, int to){
        if (from > to || from < 0){
            throw new InvalidRangeException("BOARD-404","Invalid range from : " + from + " to : " + to);
        }
    }

    private int adjustEndIdx(int from, int to, int maxIdx){
        if (maxIdx < to){
            to = maxIdx;
        }
        if (to - from + 1 > perPage){
            return from + perPage - 1;
        }
        return to;
    }

    private List<BoardDto> setCategoryName(List<BoardDto> boardDtoList){
        List<BoardDto> updatedBoardDtoList = new ArrayList<>();
        for (BoardDto boardDto : boardDtoList){
            CategoryDto categoryDto = boardInCategoryRepository.inquiryCategoryById(boardDto.getCategoryId());
            boardDto.setCategoryName(categoryDto.getCategoryName());
            updatedBoardDtoList.add(boardDto);
        }
        return updatedBoardDtoList;
    }

    @Override
    public List<BoardDto> inquiryAllBoardByCategory(int categoryId, int from, int to){
        throwIfCategoryDoesNotExist(categoryId);
        throwIfInvalidRangeDetected(from, to);
        int maxIdx = boardRepository.getBoardMaxIdxByCategory(categoryId);

        to = adjustEndIdx(from, to, maxIdx);
        List<BoardDto> boardDtoList = boardRepository.inquiryAllBoardByCategory(categoryId, from, to);
        return setCategoryName(boardDtoList);
    }

    @Override
    public List<BoardDto> searchBoardByTitle(String keyword, int from, int to){
        throwIfInvalidRangeDetected(from, to);
        int maxIdx = boardRepository.getBoardMaxIdxByTitle(keyword);
        to = adjustEndIdx(from, to, maxIdx);
        List<BoardDto> boardDtoList = boardRepository.searchByTitle(keyword, from, to);
        return setCategoryName(boardDtoList);
    }

    @Override
    public List<BoardDto> searchBoardByContent(String keyword, int from, int to){
        throwIfInvalidRangeDetected(from, to);
        int maxIdx = boardRepository.getBoardMaxIdxByContent(keyword);
        to = adjustEndIdx(from, to, maxIdx);
        List<BoardDto> boardDtoList = boardRepository.searchByContent(keyword, from, to);
        return setCategoryName(boardDtoList);
    }

    @Override
    public List<BoardDto> searchBoardByAuthorUserId(String keyword, int from, int to){
        throwIfInvalidRangeDetected(from, to);
        int maxIdx = boardRepository.getBoardMaxIdxByAuthorUserId(keyword);
        to = adjustEndIdx(from, to, maxIdx);
        List<BoardDto> boardDtoList = boardRepository.searchByAuthorUserId(keyword, from, to);
        return setCategoryName(boardDtoList);
    }

    @Override
    public void modifyBoard(BoardRequest boardRequest){
        throwIfBoardDoesNotExist(boardRequest.getId());
        throwIfCategoryDoesNotExist(boardRequest.getCategoryId());
        BoardDto boardDto = boardRepository.inquiryBoardById(boardRequest.getId());
        BoardDto updatedBoardDto = updateBoardDto(boardDto, boardRequest);
        boardRepository.modifyBoard(updatedBoardDto);
    }

    private BoardDto updateBoardDto(BoardDto boardDto, BoardRequest boardRequest){
        deleteFilePathList(boardDto, boardRequest);
        setBoardDto(boardDto, boardRequest);
        addSavedFilePathList(boardDto, boardRequest);
        return boardDto;
    }

    private BoardDto deleteFilePathList(BoardDto boardDto, BoardRequest boardRequest){
        Future<Boolean> future = fileInfoGettable.isFileDeleteSuccess();
        throwIfFileDoesNotDeleted(future);
        List<String> deleteFileNameList = boardRequest.getDeleteFileNameList();
        List<String> updatedFilePaths = deleteFilePath(boardDto.getFileUrls(), deleteFileNameList);
        List<String> updatedImagePaths = deleteFilePath(boardDto.getImageUrls(), deleteFileNameList);
        boardDto.setFileUrls(updatedFilePaths);
        boardDto.setImageUrls(updatedImagePaths);
        return boardDto;
    }

    private void throwIfFileDoesNotDeleted(Future<Boolean> future){
        try{
            future.get();
        } catch (InterruptedException | ExecutionException e){
            throw new FileDoesNotSavedException("BOARD-502","File does not saved");
        }

    }

    private List<String> deleteFilePath(List<String> beforeFilePathList, List<String> requestFilePathList){
        for (String requestFilePath : requestFilePathList){
            beforeFilePathList.remove(requestFilePath);
        }
        return beforeFilePathList;
    }

    private BoardDto setBoardDto(BoardDto boardDto, BoardRequest boardRequest){
        boardDto.setCategoryId(boardRequest.getCategoryId());
        boardDto.setTitle(boardRequest.getTitle());
        boardDto.setContent(boardRequest.getContent());
        boardDto.setLastModifiedAt(LocalDateTime.now());
        return boardDto;
    }

    private BoardDto addSavedFilePathList(BoardDto boardDto, BoardRequest boardRequest){
        List<String> saveFileNameList = getFileNameList();
        List<String> saveImageNameList = getImageNameList();
        List<String> filePathList = boardDto.getFileUrls();
        List<String> imagePathList = boardDto.getImageUrls();
        boardDto.setFileUrls(saveFilePath(filePathList, saveFileNameList));
        boardDto.setImageUrls(saveFilePath(imagePathList, saveImageNameList));
        return boardDto;
    }

    private List<String> saveFilePath(List<String> beforeFilePathList, List<String> requestFilePathList){
        for (String requestFilePath : requestFilePathList){
            beforeFilePathList.add(requestFilePath);
        }
        return beforeFilePathList;
    }

    @Override
    public void deleteBoard(int boardId){
        throwIfBoardDoesNotExist(boardId);
        boardRepository.deleteBoard(boardId);
    }

}
