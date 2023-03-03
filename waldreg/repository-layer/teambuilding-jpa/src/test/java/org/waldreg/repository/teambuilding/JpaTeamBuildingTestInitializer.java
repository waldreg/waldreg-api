package org.waldreg.repository.teambuilding;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("org.waldreg.domain.*")
class JpaTeamBuildingTestInitializer{
}
