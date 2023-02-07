package org.waldreg.controller.board;

import java.io.File;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.board.file.FileManager;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.token.aop.annotation.Authenticating;

@RestController
public class FileController{

    private FileManager fileManager;

    public FileController(FileManager fileManager){
        this.fileManager = fileManager;
    }

    @Authenticating
    @GetMapping("/image/{image-name}")
    public @ResponseBody byte[] getImage(@PathVariable("image-name") String imageName){
        return fileManager.getFileIntoByteArray(imageName);
    }

    @Authenticating
    @PermissionVerifying(value = "File download manager")
    @GetMapping("/file/{file-name}")
    public ResponseEntity<Resource> getFile(@PathVariable("file-name") String fileName){
        File file =  fileManager.getFileIntoFile(fileName);
        Resource resource = new FileSystemResource(file);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

}
