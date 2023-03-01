package org.waldreg.repository.reward;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("org.waldreg.domain.*")
public class JpaRewardTestInitializer{
}
