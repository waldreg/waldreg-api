package org.waldreg.board.file;

import java.util.concurrent.Future;
import org.springframework.web.multipart.MultipartFile;

public interface FileManager{

    Future<String> saveFile(MultipartFile multipartFile);

    Future<Boolean> renameFile(String targetPath, String renameTo);

}
