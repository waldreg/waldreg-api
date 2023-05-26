package org.waldreg.board.file;

import org.springframework.stereotype.Service;
import org.waldreg.board.file.spi.BoardFileNameRepository;

@Service
public class DefaultFileNameManager implements FileNameManger{

    private final BoardFileNameRepository boardFileNameRepository;

    public DefaultFileNameManager(BoardFileNameRepository boardFileNameRepository){
        this.boardFileNameRepository = boardFileNameRepository;
    }


    @Override
    public String getFileNameOriginByUuid(String uuid){
        return boardFileNameRepository.getOriginByUUID(uuid);
    }

}
