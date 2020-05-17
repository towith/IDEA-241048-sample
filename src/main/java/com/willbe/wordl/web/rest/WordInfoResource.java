package com.willbe.wordl.web.rest;

import com.willbe.wordl.domain.WordInfo;
import com.willbe.wordl.repository.WordInfoRepository;
import com.willbe.wordl.repository.search.WordInfoSearchRepository;
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
 * REST controller for managing {@link com.willbe.wordl.domain.WordInfo}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class WordInfoResource {

    private final Logger log = LoggerFactory.getLogger(WordInfoResource.class);

    private static final String ENTITY_NAME = "wordInfo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WordInfoRepository wordInfoRepository;

    private final WordInfoSearchRepository wordInfoSearchRepository;

    public WordInfoResource(WordInfoRepository wordInfoRepository, WordInfoSearchRepository wordInfoSearchRepository) {
        this.wordInfoRepository = wordInfoRepository;
        this.wordInfoSearchRepository = wordInfoSearchRepository;
    }

    /**
     * {@code POST  /word-infos} : Create a new wordInfo.
     *
     * @param wordInfo the wordInfo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new wordInfo, or with status {@code 400 (Bad Request)} if the wordInfo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/word-infos")
    public ResponseEntity<WordInfo> createWordInfo(@RequestBody WordInfo wordInfo) throws URISyntaxException {
        log.debug("REST request to save WordInfo : {}", wordInfo);
        if (wordInfo.getId() != null) {
            throw new BadRequestAlertException("A new wordInfo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WordInfo result = wordInfoRepository.save(wordInfo);
        wordInfoSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/word-infos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /word-infos} : Updates an existing wordInfo.
     *
     * @param wordInfo the wordInfo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wordInfo,
     * or with status {@code 400 (Bad Request)} if the wordInfo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the wordInfo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/word-infos")
    public ResponseEntity<WordInfo> updateWordInfo(@RequestBody WordInfo wordInfo) throws URISyntaxException {
        log.debug("REST request to update WordInfo : {}", wordInfo);
        if (wordInfo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        WordInfo result = wordInfoRepository.save(wordInfo);
        wordInfoSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wordInfo.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /word-infos} : get all the wordInfos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of wordInfos in body.
     */
    @GetMapping("/word-infos")
    public List<WordInfo> getAllWordInfos() {
        log.debug("REST request to get all WordInfos");
        return wordInfoRepository.findAll();
    }

    /**
     * {@code GET  /word-infos/:id} : get the "id" wordInfo.
     *
     * @param id the id of the wordInfo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the wordInfo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/word-infos/{id}")
    public ResponseEntity<WordInfo> getWordInfo(@PathVariable Long id) {
        log.debug("REST request to get WordInfo : {}", id);
        Optional<WordInfo> wordInfo = wordInfoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(wordInfo);
    }

    /**
     * {@code DELETE  /word-infos/:id} : delete the "id" wordInfo.
     *
     * @param id the id of the wordInfo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/word-infos/{id}")
    public ResponseEntity<Void> deleteWordInfo(@PathVariable Long id) {
        log.debug("REST request to delete WordInfo : {}", id);
        wordInfoRepository.deleteById(id);
        wordInfoSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/word-infos?query=:query} : search for the wordInfo corresponding
     * to the query.
     *
     * @param query the query of the wordInfo search.
     * @return the result of the search.
     */
    @GetMapping("/_search/word-infos")
    public List<WordInfo> searchWordInfos(@RequestParam String query) {
        log.debug("REST request to search WordInfos for query {}", query);
        return StreamSupport
            .stream(wordInfoSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
