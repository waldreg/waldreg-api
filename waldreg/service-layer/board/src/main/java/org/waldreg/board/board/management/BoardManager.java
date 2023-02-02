package org.waldreg.board.board.management;

import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.BoardServiceMemberTier;

public interface BoardManager{

    BoardDto createBoard(BoardRequest request);


    final class BoardRequest{

        private int authorId;
        private String title;
        private int categoryId;
        private String content;
        private BoardServiceMemberTier boardServiceMemberTier;
        private int fileCount;
        private int imageCount;

        private BoardRequest(){}

        private BoardRequest(Builder builder){
            this.authorId = builder.authorId;
            this.categoryId = builder.categoryId;
            this.title = builder.title;
            this.content = builder.content;
            this.boardServiceMemberTier = builder.boardServiceMemberTier;
            this.fileCount = builder.fileCount;
            this.imageCount = builder.imageCount;
        }

        public static Builder builder(){
            return new Builder();
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

        public BoardServiceMemberTier getMemberTier(){
            return boardServiceMemberTier;
        }

        public void setMemberTier(BoardServiceMemberTier boardServiceMemberTier){
            this.boardServiceMemberTier = boardServiceMemberTier;
        }

        public int getFileCount(){
            return fileCount;
        }

        public void setFileCount(int fileCount){
            this.fileCount = fileCount;
        }

        public int getImageCount(){
            return imageCount;
        }

        public void setImageCount(int imageCount){
            this.imageCount = imageCount;
        }

        public static final class Builder{

            private int authorId;
            private String title;
            private int categoryId;
            private String content;
            private BoardServiceMemberTier boardServiceMemberTier;
            private int fileCount;
            private int imageCount;

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

            public Builder memberTier(BoardServiceMemberTier boardServiceMemberTier){
                this.boardServiceMemberTier = boardServiceMemberTier;
                return this;
            }

            public Builder fileCount(int fileCount){
                this.fileCount = fileCount;
                return this;
            }

            public Builder imageCount(int imageCount){
                this.imageCount = imageCount;
                return this;
            }

            public BoardRequest build(){
                return new BoardRequest(this);
            }

        }

    }


}
