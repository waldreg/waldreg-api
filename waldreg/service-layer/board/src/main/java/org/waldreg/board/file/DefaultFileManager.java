package org.waldreg.board.file;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DefaultFileManager implements FileManager{

    @Value("${file.path}")
    private String path;
    private final ExecutorService executorService;

    public DefaultFileManager(){
        executorService = Executors.newFixedThreadPool(3);
    }

    @Override
    public Future<String> saveFile(MultipartFile multipartFile){
        System.out.println("path : " + path);
        Callable<String> callable = createCallable(multipartFile);
        return executorService.submit(callable);
    }

    private Callable<String> createCallable(MultipartFile multipartFile){
        return ()->{
            String id = UUID.randomUUID().toString();
            String path = getPath(id, multipartFile);
            Path filePath = Paths.get(path);
            Path target = Files.createFile(filePath);
            multipartFile.transferTo(target);
            return id;
        };
    }

    private String getPath(String id, MultipartFile multipartFile){
        StringBuilder stringBuilder = new StringBuilder();
        MimeType mimeType = MimeTypeUtils.parseMimeType(multipartFile.getContentType());
        stringBuilder.append(path).append(id).append(".").append(mimeType.getSubtype());
        return stringBuilder.toString();
    }

}