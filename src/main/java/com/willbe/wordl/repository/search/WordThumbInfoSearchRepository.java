package com.willbe.wordl.repository.search;

import com.willbe.wordl.domain.WordThumbInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link WordThumbInfo} entity.
 */
public interface WordThumbInfoSearchRepository extends ElasticsearchRepository<WordThumbInfo, Long> {
}
