package com.willbe.wordl.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link CategoryWordSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class CategoryWordSearchRepositoryMockConfiguration {

    @MockBean
    private CategoryWordSearchRepository mockCategoryWordSearchRepository;

}
