package org.waldreg.board.file;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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
import org.waldreg.board.file.exception.DuplicateFileId;
import org.waldreg.board.file.exception.UnknownFileId;

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
        Callable<String> callable = createCallable(multipartFile);
        return executorService.submit(callable);
    }

    private Callable<String> createCallable(MultipartFile multipartFile){
        return () -> {
            String id = UUID.randomUUID().toString();
            String path = getPath(id, multipartFile);
            Path filePath = Paths.get(path);
            Path target = Files.createFile(filePath);
            multipartFile.transferTo(target);
            return path;
        };
    }

    private String getPath(String id, MultipartFile multipartFile){
        StringBuilder stringBuilder = new StringBuilder();
        MimeType mimeType = MimeTypeUtils.parseMimeType(multipartFile.getContentType());
        stringBuilder.append(path).append(id).append(".").append(mimeType.getSubtype());
        return stringBuilder.toString();
    }

    @Override
    public Future<Boolean> renameFile(String targetPath, String renameTo){
        Callable<Boolean> callable = renameCallable(targetPath, renameTo);
        return executorService.submit(callable);
    }

    private Callable<Boolean> renameCallable(String targetPath, String renameTo){
        return () -> {
            try{
                Path existSource = Paths.get(targetPath);
                Files.move(existSource, existSource.resolveSibling(renameTo));
            } catch (FileAlreadyExistsException FAEE){
                throw new DuplicateFileId(renameTo);
            }
            return true;
        };
    }

    @Override
    public Future<Boolean> deleteFile(String target){
        Callable<Boolean> callable = deleteCallable(target);
        return executorService.submit(callable);
    }

    private Callable<Boolean> deleteCallable(String target){
        return () -> {
            try{
                String path = getPath(target);
                Path existSource = Paths.get(path);
                Files.delete(existSource);
            } catch (InvalidPathException IPE){
                throw new UnknownFileId(target);
            }
            return true;
        };
    }

    private String getPath(String id){
        return path + id;
    }

}