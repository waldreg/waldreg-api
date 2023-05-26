package org.waldreg.repository.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.board.file.spi.BoardFileNameRepository;
import org.waldreg.domain.board.file.FileName;
import org.waldreg.repository.board.repository.JpaFileNameRepository;

@Repository
public class FileNameCommander implements BoardFileNameRepository{

    private final JpaFileNameRepository jpaFileNameRepository;

    @Autowired
    FileNameCommander(JpaFileNameRepository jpaFileNameRepository){
        this.jpaFileNameRepository = jpaFileNameRepository;
    }

    @Override
    @Transactional
    public void saveFileName(String origin, String uuid){
        FileName fileName = FileName.builder()
                .origin(origin)
                .uuid(uuid)
                .build();
        jpaFileNameRepository.save(fileName);
    }

    @Override
    @Transactional
    public String getUUIDByOrigin(String origin){
        FileName fileName = jpaFileNameRepository.getFileNameByOrigin(origin).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find file name origin: \"" + origin + "\"");}
        );
        return fileName.getUuid();
    }

    @Override
    public String getOriginByUUID(String uuid){
        FileName fileName = jpaFileNameRepository.getFileNameByUuid(uuid).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find file name uuid : \"" + uuid + "\"");}
        );
        return fileName.getOrigin();
    }

}
