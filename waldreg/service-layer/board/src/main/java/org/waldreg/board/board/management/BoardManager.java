package org.waldreg.board.board.management;

import java.util.List;
import java.util.UUID;
import org.waldreg.board.dto.BoardDto;

public interface BoardManager{

    void createBoard(BoardRequest request);

    BoardDto inquiryBoardById(int id);

    List<BoardDto> inquiryAllBoard(int from, int to);

    List<BoardDto> inquiryAllBoardByCategory(int categoryId, int from, int to);

    List<BoardDto> searchBoardByTitle(String keyword, int from, int to);

    List<BoardDto> searchBoardByContent(String keyword, int from, int to);

    List<BoardDto> searchBoardByAuthorUserId(String keyword, int from, int to);

    void modifyBoard(BoardRequest boardRequest);

    void deleteBoard(int boardId);


    final class BoardRequest{

        private int id;
        private int authorId;
        private String title;
        private int categoryId;
        private String content;
        private List<UUID> fileUuidList;
        private List<UUID> imageUuidList;
        private List<UUID> deleteFileNameList;

        private BoardRequest(){}

        private BoardRequest(Builder builder){
            this.id = builder.id;
            this.authorId = builder.authorId;
            this.categoryId = builder.categoryId;
            this.title = builder.title;
            this.content = builder.content;
            this.fileUuidList = builder.fileUuidList;
            this.imageUuidList = builder.imageUuidList;
            this.deleteFileNameList = builder.deleteFileNameList;
        }

        public static Builder builder(){
            return new Builder();
        }

        public int getId(){
            return id;
        }

        public int getAuthorId(){
            return authorId;
        }

        public void setAuthorId(int authorId){
            this.authorId = authorId;
        }

        public String getTitle(){
            return title;
        }

        public void setTitle(String title){
            this.title = title;
        }

        public int getCategoryId(){
            return categoryId;
        }

        public void setCategoryId(int categoryId){
            this.categoryId = categoryId;
        }

        public String getContent(){
            return content;
        }

        public void setContent(String content){
            this.content = content;
        }

        public List<UUID> getFileUuidList(){
            return fileUuidList;
        }

        public void setFileUuidList(List<UUID> fileUuidList){
            this.fileUuidList = fileUuidList;
        }

        public List<UUID> getImageUuidList(){
            return imageUuidList;
        }

        public void setImageUuidList(List<UUID> imageUuidList){
            this.imageUuidList = imageUuidList;
        }

        public List<UUID> getDeleteFileNameList(){
            return deleteFileNameList;
        }

        public static final class Builder{

            private int id;
            private int authorId;
            private String title;
            private int categoryId;
            private String content;
            private List<UUID> fileUuidList;
            private List<UUID> imageUuidList;
            private List<UUID> deleteFileNameList;

            public Builder id(int id){
                this.id = id;
                return this;
            }

            public Builder authorId(int authorId){
                this.authorId = authorId;
                return this;
            }

            public Builder title(String title){
                this.title = title;
                return this;
            }

            public Builder categoryId(int categoryId){
                this.categoryId = categoryId;
                return this;
            }

            public Builder content(String content){
                this.content = content;
                return this;
            }

            public Builder fileUuidList(List<UUID> fileUuidList){
                this.fileUuidList = fileUuidList;
                return this;
            }

            public Builder imageUuidList(List<UUID> imageUuidList){
                this.imageUuidList = imageUuidList;
                return this;
            }

            public Builder deleteFileNameList(List<UUID> deleteFileNameList){
                this.deleteFileNameList = deleteFileNameList;
                return this;
            }

            public BoardRequest build(){
                return new BoardRequest(this);
            }

        }

    }


}
