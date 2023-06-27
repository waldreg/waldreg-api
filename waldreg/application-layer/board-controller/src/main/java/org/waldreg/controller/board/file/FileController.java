package org.waldreg.controller.board.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.stage.xss.core.meta.Xss;
import org.waldreg.board.file.FileManager;
import org.waldreg.board.file.FileNameManger;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.token.aop.annotation.Authenticating;

@RestController
public class FileController{

    private FileManager fileManager;

    private FileNameManger fileNameManger;

    @Autowired
    public FileController(FileManager fileManager, FileNameManger fileNameManger){
        this.fileManager = fileManager;
        this.fileNameManger = fileNameManger;
    }

    @Authenticating
    @PermissionVerifying(value = "Image manager")
    @GetMapping("/image/{image-name}")
    public @ResponseBody byte[] getImage(@PathVariable("image-name") String imageName){
        return fileManager.getFileIntoByteArray(imageName);
    }

    @Authenticating
    @PermissionVerifying(value = "File download manager")
    @GetMapping("/file/{file-name}")
    public ResponseEntity<Resource> getFile(@PathVariable("file-name") String fileName){
        File file =  fileManager.getFileIntoFile(fileName);
        File outputFile = new File(fileNameManger.getFileNameOriginByUuid(fileName));
        copyFile(file,outputFile);
        Resource resource = new FileSystemResource(outputFile);
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    private void copyFile(File from, File to){
        try{
            Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch (Exception e){
            throw new IllegalStateException("Cannot Copy File : " + from.getName());
        }
    }

}
