package org.waldreg.board.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
        ImageDetector imageDetector = new ImageDetector();
        Callable<String> callable = createCallable(imageDetector, multipartFile);
        System.out.println("imageDectector : " + imageDetector.isImage);
        if(imageDetector.isImage) {
            fileData.addImageName(executorService.submit(callable));
            return;
        }
        fileData.addFileName(executorService.submit(callable));
    }

    private Callable<String> createCallable(ImageDetector imageDetector, MultipartFile multipartFile){
        return () -> {
            NameWithPath nameWithPath = createFile(multipartFile);
            multipartFile.transferTo(nameWithPath.path);
            imageDetector.isImage = nameWithPath.isImage;
            System.out.println("name with path : " + nameWithPath.isImage);
            return nameWithPath.name;
        };
    }

    private NameWithPath createFile(MultipartFile multipartFile){
        try{
            String id = UUID.randomUUID().toString();
            Path filePath = Paths.get(getPath(id, multipartFile));
            Path ans = Files.createFile(filePath);
            String[] fileNameAndMimeType = getFileNameAndMimeType(id, multipartFile);
            return new NameWithPath(fileNameAndMimeType[0], fileNameAndMimeType[1].equals("image"), ans);
        } catch (FileAlreadyExistsException faee){
            return createFile(multipartFile);
        } catch (IOException ioe){
            throw new IllegalStateException("Something went wrong in progress \"createFile()\" cause " + ioe.getMessage());
        }
    }

    private String getPath(String id, MultipartFile multipartFile){
        return path + getFileNameAndMimeType(id, multipartFile)[0];
    }

    private String[] getFileNameAndMimeType(String id, MultipartFile multipartFile){
        StringBuilder stringBuilder = new StringBuilder();
        MimeType mimeType = MimeTypeUtils.parseMimeType(multipartFile.getContentType());
        String[] ans = new String[2];
        stringBuilder.append(id).append(".").append(mimeType.getSubtype());
        ans[0] = stringBuilder.toString();
        ans[1] = mimeType.getType();
        return ans;
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

    private static final class NameWithPath{

        private final String name;
        private final boolean isImage;
        private final Path path;

        private NameWithPath(String name, boolean isImage, Path path){
            this.name = name;
            this.isImage = isImage;
            this.path = path;
        }

    }

    private static final class ImageDetector{

        private boolean isImage;

    }

}