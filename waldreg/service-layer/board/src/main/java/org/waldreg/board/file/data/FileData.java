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

    private List<Future<String>> fileName = new ArrayList<>();
    private Future<Boolean> isDeleted;

    @Override
    public List<Future<String>> getSavedFileName(){
        return fileName;
    }

    @Override
    public Future<Boolean> isFileDeleteSuccess(){
        return isDeleted;
    }

    public void addFileName(Future<String> fileName){
        this.fileName.add(fileName);
    }

    public void setIsDeleted(Future<Boolean> isDeleted){
        this.isDeleted = isDeleted;
    }

}
