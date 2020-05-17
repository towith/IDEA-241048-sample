package com.willbe.wordl.repository.search;

import com.willbe.wordl.domain.WordInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link WordInfo} entity.
 */
public interface WordInfoSearchRepository extends ElasticsearchRepository<WordInfo, Long> {
}
