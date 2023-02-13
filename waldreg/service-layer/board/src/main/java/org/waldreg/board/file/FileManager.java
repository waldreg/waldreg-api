package org.waldreg.board.file;

import java.io.File;
import java.util.concurrent.Future;
import org.springframework.web.multipart.MultipartFile;

public interface FileManager{

    /**
     * MultiPartFile를 인자로 받아 저장후, 저장된 파일의 이름을 FileData에 전달함.
     * <br>
     * 저장된 File의 이름은 FileInfoGettable을 주입받아 사용 가능함.
     * ex) abc.png
     *
     * @param multipartFile 저장할 파일
     */
    void saveFile(MultipartFile multipartFile);

    /**
     * 삭제할 파일의 이름을 인자로 받아 삭제함.
     * <br>
     * 파일을 삭제후, 삭제 유무를 FileData에 저장함.
     *
     * @param target 삭제 할 파일의 이름 ex) 1.png
     */
    void deleteFile(String target);

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
