package com.willbe.wordl.web.rest;

import com.willbe.wordl.domain.UserThumbInfo;
import com.willbe.wordl.repository.UserThumbInfoRepository;
import com.willbe.wordl.repository.search.UserThumbInfoSearchRepository;
import com.willbe.wordl.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * REST controller for managing {@link com.willbe.wordl.domain.UserThumbInfo}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UserThumbInfoResource {

    private final Logger log = LoggerFactory.getLogger(UserThumbInfoResource.class);

    private static final String ENTITY_NAME = "userThumbInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserThumbInfoRepository userThumbInfoRepository;

    private final UserThumbInfoSearchRepository userThumbInfoSearchRepository;

    public UserThumbInfoResource(UserThumbInfoRepository userThumbInfoRepository, UserThumbInfoSearchRepository userThumbInfoSearchRepository) {
        this.userThumbInfoRepository = userThumbInfoRepository;
        this.userThumbInfoSearchRepository = userThumbInfoSearchRepository;
    }

    /**
     * {@code POST  /user-thumb-infos} : Create a new userThumbInfo.
     *
     * @param userThumbInfo the userThumbInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userThumbInfo, or with status {@code 400 (Bad Request)} if the userThumbInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-thumb-infos")
    public ResponseEntity<UserThumbInfo> createUserThumbInfo(@RequestBody UserThumbInfo userThumbInfo) throws URISyntaxException {
        log.debug("REST request to save UserThumbInfo : {}", userThumbInfo);
        if (userThumbInfo.getId() != null) {
            throw new BadRequestAlertException("A new userThumbInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserThumbInfo result = userThumbInfoRepository.save(userThumbInfo);
        userThumbInfoSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/user-thumb-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-thumb-infos} : Updates an existing userThumbInfo.
     *
     * @param userThumbInfo the userThumbInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userThumbInfo,
     * or with status {@code 400 (Bad Request)} if the userThumbInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userThumbInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-thumb-infos")
    public ResponseEntity<UserThumbInfo> updateUserThumbInfo(@RequestBody UserThumbInfo userThumbInfo) throws URISyntaxException {
        log.debug("REST request to update UserThumbInfo : {}", userThumbInfo);
        if (userThumbInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserThumbInfo result = userThumbInfoRepository.save(userThumbInfo);
        userThumbInfoSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userThumbInfo.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /user-thumb-infos} : get all the userThumbInfos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userThumbInfos in body.
     */
    @GetMapping("/user-thumb-infos")
    public List<UserThumbInfo> getAllUserThumbInfos() {
        log.debug("REST request to get all UserThumbInfos");
        return userThumbInfoRepository.findAll();
    }

    /**
     * {@code GET  /user-thumb-infos/:id} : get the "id" userThumbInfo.
     *
     * @param id the id of the userThumbInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userThumbInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-thumb-infos/{id}")
    public ResponseEntity<UserThumbInfo> getUserThumbInfo(@PathVariable Long id) {
        log.debug("REST request to get UserThumbInfo : {}", id);
        Optional<UserThumbInfo> userThumbInfo = userThumbInfoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(userThumbInfo);
    }

    /**
     * {@code DELETE  /user-thumb-infos/:id} : delete the "id" userThumbInfo.
     *
     * @param id the id of the userThumbInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-thumb-infos/{id}")
    public ResponseEntity<Void> deleteUserThumbInfo(@PathVariable Long id) {
        log.debug("REST request to delete UserThumbInfo : {}", id);
        userThumbInfoRepository.deleteById(id);
        userThumbInfoSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/user-thumb-infos?query=:query} : search for the userThumbInfo corresponding
     * to the query.
     *
     * @param query the query of the userThumbInfo search.
     * @return the result of the search.
     */
    @GetMapping("/_search/user-thumb-infos")
    public List<UserThumbInfo> searchUserThumbInfos(@RequestParam String query) {
        log.debug("REST request to search UserThumbInfos for query {}", query);
        return StreamSupport
            .stream(userThumbInfoSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
