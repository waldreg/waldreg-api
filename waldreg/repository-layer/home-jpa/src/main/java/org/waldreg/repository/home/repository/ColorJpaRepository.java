package org.waldreg.repository.home.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.home.ApplicationColor;

@Repository
public interface ColorJpaRepository extends JpaRepository<ApplicationColor, Integer>{

}
