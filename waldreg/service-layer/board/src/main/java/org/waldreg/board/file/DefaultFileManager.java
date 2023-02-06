package org.waldreg.board.file;

import java.io.File;
import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import org.waldreg.board.file.data.FileData;
import org.waldreg.board.file.exception.DuplicateFileId;
import org.waldreg.board.file.exception.UnknownFileId;

@Service
public class DefaultFileManager implements FileManager{

    @Value("${file.path}")
    private String path;
    private final ExecutorService executorService;
    private final FileData fileData;

    @Autowired
    public DefaultFileManager(FileData fileData){
        this.fileData = fileData;
        executorService = Executors.newFixedThreadPool(3);
    }

    @Override
    public void saveFile(MultipartFile multipartFile){
        Callable<String> callable = createCallable(multipartFile);
        fileData.addFileName(executorService.submit(callable));
    }

    private Callable<String> createCallable(MultipartFile multipartFile){
        return () -> {
            NameWithPath nameWithPath = createFile(multipartFile);
            multipartFile.transferTo(nameWithPath.path);
            return nameWithPath.name;
        };
    }

    private NameWithPath createFile(MultipartFile multipartFile){
        try{
            String id = UUID.randomUUID().toString();
            Path filePath = Paths.get(getPath(id, multipartFile));
            Path ans = Files.createFile(filePath);
            return new NameWithPath(getFileNameWithMimeType(id, multipartFile), ans);
        } catch (FileAlreadyExistsException faee){
            return createFile(multipartFile);
        } catch (IOException ioe){
            throw new IllegalStateException("Something went wrong in progress \"createFile()\" cause " + ioe.getMessage());
        }
    }

    private String getPath(String id, MultipartFile multipartFile){
        return path + getFileNameWithMimeType(id, multipartFile);
    }

    private String getFileNameWithMimeType(String id, MultipartFile multipartFile){
        StringBuilder stringBuilder = new StringBuilder();
        MimeType mimeType = MimeTypeUtils.parseMimeType(multipartFile.getContentType());
        stringBuilder.append(id).append(".").append(mimeType.getSubtype());
        return stringBuilder.toString();
    }

    @Override
    public void deleteFile(String target){
        Callable<Boolean> callable = deleteCallable(target);
        fileData.setIsDeleted(executorService.submit(callable));
    }

    private Callable<Boolean> deleteCallable(String target){
        return () -> {
            try{
                Path existSource = Paths.get(getPath(target));
                Files.delete(existSource);
            } catch (InvalidPathException ipe){
                throw new UnknownFileId(target);
            }
            return true;
        };
    }

    @Override
    public byte[] getFileIntoByteArray(String target){
        try{
            Path existSource = Paths.get(getPath(target));
            return Files.readAllBytes(existSource);
        } catch(InvalidPathException ipe){
            throw new UnknownFileId(target);
        }
        catch(Exception e){
            throw new IllegalStateException(e);
        }
    }

    @Override
    public File getFileIntoFile(String target){
        try{
            Path existSource = Paths.get(getPath(target));
            return existSource.toFile();
        } catch(InvalidPathException ipe){
            throw new UnknownFileId(target);
        }
        catch(Exception e){
            throw new IllegalStateException(e);
        }
    }

    private String getPath(String id){
        return path + id;
    }

    private final static class NameWithPath{

        private final String name;
        private final Path path;

        private NameWithPath(String name, Path path){
            this.name = name;
            this.path = path;
        }

    }

}