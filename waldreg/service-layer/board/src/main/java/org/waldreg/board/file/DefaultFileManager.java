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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;
import org.waldreg.board.file.data.FileData;
import org.waldreg.board.file.exception.UnknownFileId;
import org.waldreg.board.file.spi.BoardFileNameRepository;

@Service
public class DefaultFileManager implements FileManager{

    @Value("${file.path:none}")
    private String path;
    private final ExecutorService executorService;
    private final FileData fileData;
    private final BoardFileNameRepository boardFileNameRepository;

    @Autowired
    public DefaultFileManager(FileData fileData, BoardFileNameRepository boardFileNameRepository, BoardFileNameRepository boardFileNameRepository1){
        this.fileData = fileData;
        this.boardFileNameRepository = boardFileNameRepository1;
        executorService = Executors.newFixedThreadPool(3);
    }

    @Override
    public void saveFile(MultipartFile multipartFile){
        Callable<String> callable = createCallable(multipartFile);
        if(isImage(multipartFile)) {
            fileData.addImageName(executorService.submit(callable));
            return;
        }
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
            String origin = getFileName(multipartFile);
            Path filePath = Paths.get(getPath(id, multipartFile));
            Path ans = Files.createFile(filePath);
            String[] originAndMimeType = getFileNameAndMimeType(origin, multipartFile);
            String[] uuidAndMimeType = getFileNameAndMimeType(id,multipartFile);
            boardFileNameRepository.saveFileName(originAndMimeType[0], uuidAndMimeType[0]);
            return new NameWithPath(uuidAndMimeType[0], ans);
        } catch (FileAlreadyExistsException faee){
            return createFile(multipartFile);
        } catch (IOException ioe){
            throw new IllegalStateException("Something went wrong in progress \"createFile()\" cause " + ioe.getMessage());
        }
    }

    private String getFileName(MultipartFile file){
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null){
            return null;
        }

        // 파일 경로 구분자에 따라서 파일 이름을 추출합니다
        // Windows: \
        // Mac: /
        int lastUnixPos = originalFilename.lastIndexOf('/');
        int lastWindowsPos = originalFilename.lastIndexOf('\\');
        int lastIndex = Math.max(lastUnixPos, lastWindowsPos);

        String fileName;
        if (lastIndex > 0){
            fileName = originalFilename.substring(lastIndex + 1);
        } else{
            fileName = originalFilename;
        }

        return fileName;
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

    private boolean isImage(MultipartFile multipartFile){
        MimeType mimeType = MimeTypeUtils.parseMimeType(multipartFile.getContentType());
        return mimeType.getType().equals("image");
    }

    @Override
    public void deleteFile(String target){
        Callable<Boolean> callable = deleteCallable(target);
        fileData.setIsDeleted(executorService.submit(callable));
        boardFileNameRepository.deleteFileNameByUUID(target);
    }

    private Callable<Boolean> deleteCallable(String target){
        return () -> {
            try{
                Path existSource = Paths.get(getPath(target));
                throwIfFileDoesNotExist(existSource, target);
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
            throwIfFileDoesNotExist(existSource, target);
            return Files.readAllBytes(existSource);
        } catch(InvalidPathException ipe){
            throw new IllegalStateException(ipe);
        } catch(Exception e){
            throw new UnknownFileId(target);
        }
    }

    @Override
    public File getFileIntoFile(String target){
        try{
            Path existSource = Paths.get(getPath(target));
            throwIfFileDoesNotExist(existSource, target);
            return existSource.toFile();
        } catch(InvalidPathException ipe){
            throw new IllegalStateException(ipe);
        } catch(Exception e){
            throw new UnknownFileId(target);
        }
    }

    private String getPath(String id){
        return path + id;
    }

    private void throwIfFileDoesNotExist(Path path, String target){
        if(!Files.exists(path)){
            throw new UnknownFileId(target);
        }
    }

    private static final class NameWithPath{

        private final String name;
        private final Path path;

        private NameWithPath(String name, Path path){
            this.name = name;
            this.path = path;
        }

    }

}