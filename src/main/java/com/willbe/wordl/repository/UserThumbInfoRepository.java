package com.willbe.wordl.repository;

import com.willbe.wordl.domain.UserThumbInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the UserThumbInfo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserThumbInfoRepository extends JpaRepository<UserThumbInfo, Long> {

    @Query("select userThumbInfo from UserThumbInfo userThumbInfo where userThumbInfo.clicker.login = ?#{principal.username}")
    List<UserThumbInfo> findByClickerIsCurrentUser();
}
