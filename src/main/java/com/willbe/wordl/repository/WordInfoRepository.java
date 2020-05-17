package com.willbe.wordl.repository;

import com.willbe.wordl.domain.WordInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the WordInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WordInfoRepository extends JpaRepository<WordInfo, Long> {
}
