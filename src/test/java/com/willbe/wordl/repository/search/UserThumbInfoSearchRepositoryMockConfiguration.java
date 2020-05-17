package com.willbe.wordl.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link UserThumbInfoSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class UserThumbInfoSearchRepositoryMockConfiguration {

    @MockBean
    private UserThumbInfoSearchRepository mockUserThumbInfoSearchRepository;

}
