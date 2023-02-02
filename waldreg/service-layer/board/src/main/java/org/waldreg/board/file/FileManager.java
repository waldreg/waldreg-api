package org.waldreg.board.file;

import java.io.File;
import java.util.concurrent.Future;
import org.springframework.web.multipart.MultipartFile;

public interface FileManager{

    /**
     * MultiPartFile를 인자로 받아 Future를 반환함.
     * <br>
     * Future로 가져오는값은 저장된 파일의 경로 + 이름(full path)임
     *
     * @param multipartFile 저장할 파일
     * @return Future
     */
    Future<String> saveFile(MultipartFile multipartFile);

    /**
     * 인자 targetPath로 이름을 변경할 파일의 경로 + 이름을 받아서 renameTo로 변경함.
     *
     * @param targetPath 이름을 변경할 파일의 경로 + 이름 ex) /Users/hello/abc.png
     * @param renameTo 변경될 이름 ex) 1.png
     * @return Future
     */
    Future<Boolean> renameFile(String targetPath, String renameTo);

    /**
     * 삭제할 파일의 이름을 인자로 받아 삭제함.
     *
     * @param target 삭제 할 파일의 이름 ex) 1.png
     * @return Future
     */
    Future<Boolean> deleteFile(String target);

    /**
     * 조회할 파일의 이름을 인자로 받아 파일을 조회함
     *
     * @param target 조회할 파일의 이름 ex) 1.png
     * @return byte[] 파일을 byte형태로 변경한 값
     */
    byte[] getFileIntoByteArray(String target);

    /**
     * 조회할 파일의 이름을 인자로 받아 파일을 조회함
     *
     * @param target 조회할 파일의 이름 ex) 1.png
     * @return File 파일을 file의 형태로 변경한 값
     */
    File getFileIntoFile(String target);

}
