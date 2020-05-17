package com.willbe.wordl.repository.search;

import com.willbe.wordl.domain.CategoryWord;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link CategoryWord} entity.
 */
public interface CategoryWordSearchRepository extends ElasticsearchRepository<CategoryWord, Long> {
}
