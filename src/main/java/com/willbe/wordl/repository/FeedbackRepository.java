package com.willbe.wordl.repository;

import com.willbe.wordl.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the Feedback entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("select feedback from Feedback feedback where feedback.creator.login = ?#{principal.username}")
    List<Feedback> findByCreatorIsCurrentUser();
}
