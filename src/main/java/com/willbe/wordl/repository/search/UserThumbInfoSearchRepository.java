package com.willbe.wordl.repository.search;

import com.willbe.wordl.domain.UserThumbInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link UserThumbInfo} entity.
 */
public interface UserThumbInfoSearchRepository extends ElasticsearchRepository<UserThumbInfo, Long> {
}
