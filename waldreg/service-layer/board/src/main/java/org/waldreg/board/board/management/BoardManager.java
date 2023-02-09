package org.waldreg.board.board.management;

import java.util.List;
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
        private List<String> deleteFileNameList;

        private BoardRequest(){}

        private BoardRequest(Builder builder){
            this.id = builder.id;
            this.authorId = builder.authorId;
            this.categoryId = builder.categoryId;
            this.title = builder.title;
            this.content = builder.content;
            this.deleteFileNameList = builder.deleteFileNameList;
        }

        public static Builder builder(){
            return new Builder();
        }

        public int getId(){
            return id;
        }

        public void setId(int id){
            this.id = id;
        }

        public int getAuthorId(){
            return authorId;
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

        public List<String> getDeleteFileNameList(){
            return deleteFileNameList;
        }

        public static final class Builder{

            private int id;
            private int authorId;
            private String title;
            private int categoryId;
            private String content;
            private List<String> deleteFileNameList;

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

            public Builder deleteFileNameList(List<String> deleteFileNameList){
                this.deleteFileNameList = deleteFileNameList;
                return this;
            }

            public BoardRequest build(){
                return new BoardRequest(this);
            }

        }

    }


}
