package com.willbe.wordl.repository;

import com.willbe.wordl.domain.CategoryWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data  repository for the CategoryWord entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoryWordRepository extends JpaRepository<CategoryWord, Long> {
}
