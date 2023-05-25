package org.waldreg.repository.board.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.board.file.FileName;

@Repository("boardJpaFileNameRepository")
public interface JpaFileNameRepository extends JpaRepository<FileName, Integer>{

    Optional<FileName> getFileNameByOrigin(String origin);

    Optional<FileName> getFileNameByUuid(String uuid);

}
