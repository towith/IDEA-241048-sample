package com.willbe.wordl.repository;

import com.willbe.wordl.domain.WordThumbInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the WordThumbInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WordThumbInfoRepository extends JpaRepository<WordThumbInfo, Long> {
}
