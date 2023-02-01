package org.waldreg.board.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public class DefaultFileManager{

    private String uploadLocation = "./src/main/java/org/waldreg/";


    public List<String> storeFiles(List<MultipartFile> files, String uploadPackageName){
        List<String> filePaths = new ArrayList<>();
        for (MultipartFile file : files){
            try{
                String uuid = getRandomUUID();
                StringBuilder uploadDir = new StringBuilder(uploadLocation).append(uploadPackageName).append("/");
                Path copyOfLocation = Paths.get(uploadDir + File.separator + uuid);
                Files.copy(file.getInputStream(), copyOfLocation, StandardCopyOption.REPLACE_EXISTING);
                filePaths.add(String.valueOf(copyOfLocation));
            } catch (IOException IOE){

            }
        }
        return filePaths;
    }


    private String getRandomUUID(){
        return UUID.randomUUID().toString();
    }

}