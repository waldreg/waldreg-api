package org.waldreg.repository.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.board.file.BoardFileNameRepository;
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
                () -> {throw new IllegalStateException("Cannot find file name \"" + origin + "\"");}
        );
        return fileName.getUuid();
    }

}
