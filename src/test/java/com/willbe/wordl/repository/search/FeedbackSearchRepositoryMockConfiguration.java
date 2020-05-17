package com.willbe.wordl.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link FeedbackSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class FeedbackSearchRepositoryMockConfiguration {

    @MockBean
    private FeedbackSearchRepository mockFeedbackSearchRepository;

}
