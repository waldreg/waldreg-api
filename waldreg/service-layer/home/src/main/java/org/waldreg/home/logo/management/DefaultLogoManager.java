package org.waldreg.home.logo.management;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DefaultLogoManager implements LogoManager{

    @Value("${logo.path:none}")
    private String path;
    private static final String LOGO_FILE_NAME = "logo";

    @Override
    public void updateLogo(MultipartFile request){
        try{
            deleteOriginalFile();
            File saveFile = new File(path, getFileNameAndMimeType(request)[0]);
            request.transferTo(saveFile);
        } catch (IOException e){
            throw new IllegalStateException("Failed to store logo file");
        }
    }

    private void deleteOriginalFile(){
        try{
            Path directoryPath = Paths.get(path);
            Path existSource = Files.walk(directoryPath).filter(path -> !Files.isDirectory(path)).findFirst().get();
            Files.delete(existSource);
        } catch (IOException e){
            throw new IllegalStateException("Failed to delete logo file");
        }
    }

    private String[] getFileNameAndMimeType(MultipartFile request){
        String fileName = getFileName(request);
        StringBuilder stringBuilder = new StringBuilder();
        MimeType mimeType = MimeTypeUtils.parseMimeType(request.getContentType());
        String[] ans = new String[2];
        stringBuilder.append(fileName).append(".").append(mimeType.getSubtype());
        ans[0] = stringBuilder.toString();
        ans[1] = mimeType.getType();
        return ans;
    }

    private String getFileName(MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String fileName = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        if (!fileName.matches(LOGO_FILE_NAME)){
            fileName = LOGO_FILE_NAME;
        }
        return fileName;
    }

    @Override
    public byte[] getLogo(){
        try{
            Path directoryPath = Paths.get(path);
            Path existSource = Files.walk(directoryPath).filter(path -> !Files.isDirectory(path)).findFirst().get();
            return Files.readAllBytes(existSource);
        } catch (IOException e){
            throw new IllegalStateException("Failed to load logo file");
        }
    }

}
