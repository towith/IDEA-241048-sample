package com.willbe.wordl.web.rest;

import com.willbe.wordl.domain.WordThumbInfo;
import com.willbe.wordl.repository.WordThumbInfoRepository;
import com.willbe.wordl.repository.search.WordThumbInfoSearchRepository;
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
 * REST controller for managing {@link com.willbe.wordl.domain.WordThumbInfo}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class WordThumbInfoResource {

    private final Logger log = LoggerFactory.getLogger(WordThumbInfoResource.class);

    private static final String ENTITY_NAME = "wordThumbInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WordThumbInfoRepository wordThumbInfoRepository;

    private final WordThumbInfoSearchRepository wordThumbInfoSearchRepository;

    public WordThumbInfoResource(WordThumbInfoRepository wordThumbInfoRepository, WordThumbInfoSearchRepository wordThumbInfoSearchRepository) {
        this.wordThumbInfoRepository = wordThumbInfoRepository;
        this.wordThumbInfoSearchRepository = wordThumbInfoSearchRepository;
    }

    /**
     * {@code POST  /word-thumb-infos} : Create a new wordThumbInfo.
     *
     * @param wordThumbInfo the wordThumbInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new wordThumbInfo, or with status {@code 400 (Bad Request)} if the wordThumbInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/word-thumb-infos")
    public ResponseEntity<WordThumbInfo> createWordThumbInfo(@RequestBody WordThumbInfo wordThumbInfo) throws URISyntaxException {
        log.debug("REST request to save WordThumbInfo : {}", wordThumbInfo);
        if (wordThumbInfo.getId() != null) {
            throw new BadRequestAlertException("A new wordThumbInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WordThumbInfo result = wordThumbInfoRepository.save(wordThumbInfo);
        wordThumbInfoSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/word-thumb-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /word-thumb-infos} : Updates an existing wordThumbInfo.
     *
     * @param wordThumbInfo the wordThumbInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wordThumbInfo,
     * or with status {@code 400 (Bad Request)} if the wordThumbInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the wordThumbInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/word-thumb-infos")
    public ResponseEntity<WordThumbInfo> updateWordThumbInfo(@RequestBody WordThumbInfo wordThumbInfo) throws URISyntaxException {
        log.debug("REST request to update WordThumbInfo : {}", wordThumbInfo);
        if (wordThumbInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        WordThumbInfo result = wordThumbInfoRepository.save(wordThumbInfo);
        wordThumbInfoSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wordThumbInfo.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /word-thumb-infos} : get all the wordThumbInfos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of wordThumbInfos in body.
     */
    @GetMapping("/word-thumb-infos")
    public List<WordThumbInfo> getAllWordThumbInfos() {
        log.debug("REST request to get all WordThumbInfos");
        return wordThumbInfoRepository.findAll();
    }

    /**
     * {@code GET  /word-thumb-infos/:id} : get the "id" wordThumbInfo.
     *
     * @param id the id of the wordThumbInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the wordThumbInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/word-thumb-infos/{id}")
    public ResponseEntity<WordThumbInfo> getWordThumbInfo(@PathVariable Long id) {
        log.debug("REST request to get WordThumbInfo : {}", id);
        Optional<WordThumbInfo> wordThumbInfo = wordThumbInfoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(wordThumbInfo);
    }

    /**
     * {@code DELETE  /word-thumb-infos/:id} : delete the "id" wordThumbInfo.
     *
     * @param id the id of the wordThumbInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/word-thumb-infos/{id}")
    public ResponseEntity<Void> deleteWordThumbInfo(@PathVariable Long id) {
        log.debug("REST request to delete WordThumbInfo : {}", id);
        wordThumbInfoRepository.deleteById(id);
        wordThumbInfoSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/word-thumb-infos?query=:query} : search for the wordThumbInfo corresponding
     * to the query.
     *
     * @param query the query of the wordThumbInfo search.
     * @return the result of the search.
     */
    @GetMapping("/_search/word-thumb-infos")
    public List<WordThumbInfo> searchWordThumbInfos(@RequestParam String query) {
        log.debug("REST request to search WordThumbInfos for query {}", query);
        return StreamSupport
            .stream(wordThumbInfoSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
