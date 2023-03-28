package org.waldreg.repository.home;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ContextConfiguration(classes = {
        JpaHomeTestInitializer.class,
        HomeRepositoryServiceProvider.class
})
@TestPropertySource("classpath:h2-application.properties")
class HomeRepositoryServiceProviderTest{

    @Autowired
    private HomeRepositoryServiceProvider homeServiceProvider;

}
