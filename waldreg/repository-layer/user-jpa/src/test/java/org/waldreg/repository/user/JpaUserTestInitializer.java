package org.waldreg.repository.user;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("org.waldreg.domain.*")
class JpaUserTestInitializer{
}
