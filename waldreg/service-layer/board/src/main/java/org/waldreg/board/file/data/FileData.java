package org.waldreg.board.file.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.waldreg.board.board.file.FileInfoGettable;

@Service
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class FileData implements FileInfoGettable{

    private List<Future<String>> fileNameList = new ArrayList<>();
    private List<Future<String>> imageNameList = new ArrayList<>();
    private Future<Boolean> isDeleted;

    @Override
    public List<Future<String>> getSavedFileNameList(){
        return fileNameList;
    }

    @Override
    public List<Future<String>> getSavedImageNameList(){
        return imageNameList;
    }

    @Override
    public Future<Boolean> isFileDeleteSuccess(){
        return isDeleted;
    }

    public void addFileName(Future<String> fileName){
        this.fileNameList.add(fileName);
    }

    public void addImageName(Future<String> imageName){
        this.imageNameList.add(imageName);
    }

    public void setIsDeleted(Future<Boolean> isDeleted){
        this.isDeleted = isDeleted;
    }

}
