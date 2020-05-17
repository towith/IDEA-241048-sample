package com.willbe.wordl.web.rest;

import com.willbe.wordl.domain.CategoryWord;
import com.willbe.wordl.repository.CategoryWordRepository;
import com.willbe.wordl.repository.search.CategoryWordSearchRepository;
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
 * REST controller for managing {@link com.willbe.wordl.domain.CategoryWord}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CategoryWordResource {

    private final Logger log = LoggerFactory.getLogger(CategoryWordResource.class);

    private static final String ENTITY_NAME = "categoryWord";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CategoryWordRepository categoryWordRepository;

    private final CategoryWordSearchRepository categoryWordSearchRepository;

    public CategoryWordResource(CategoryWordRepository categoryWordRepository, CategoryWordSearchRepository categoryWordSearchRepository) {
        this.categoryWordRepository = categoryWordRepository;
        this.categoryWordSearchRepository = categoryWordSearchRepository;
    }

    /**
     * {@code POST  /category-words} : Create a new categoryWord.
     *
     * @param categoryWord the categoryWord to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new categoryWord, or with status {@code 400 (Bad Request)} if the categoryWord has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/category-words")
    public ResponseEntity<CategoryWord> createCategoryWord(@RequestBody CategoryWord categoryWord) throws URISyntaxException {
        log.debug("REST request to save CategoryWord : {}", categoryWord);
        if (categoryWord.getId() != null) {
            throw new BadRequestAlertException("A new categoryWord cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CategoryWord result = categoryWordRepository.save(categoryWord);
        categoryWordSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/category-words/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /category-words} : Updates an existing categoryWord.
     *
     * @param categoryWord the categoryWord to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated categoryWord,
     * or with status {@code 400 (Bad Request)} if the categoryWord is not valid,
     * or with status {@code 500 (Internal Server Error)} if the categoryWord couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/category-words")
    public ResponseEntity<CategoryWord> updateCategoryWord(@RequestBody CategoryWord categoryWord) throws URISyntaxException {
        log.debug("REST request to update CategoryWord : {}", categoryWord);
        if (categoryWord.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CategoryWord result = categoryWordRepository.save(categoryWord);
        categoryWordSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, categoryWord.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /category-words} : get all the categoryWords.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of categoryWords in body.
     */
    @GetMapping("/category-words")
    public List<CategoryWord> getAllCategoryWords() {
        log.debug("REST request to get all CategoryWords");
        return categoryWordRepository.findAll();
    }

    /**
     * {@code GET  /category-words/:id} : get the "id" categoryWord.
     *
     * @param id the id of the categoryWord to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the categoryWord, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/category-words/{id}")
    public ResponseEntity<CategoryWord> getCategoryWord(@PathVariable Long id) {
        log.debug("REST request to get CategoryWord : {}", id);
        Optional<CategoryWord> categoryWord = categoryWordRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(categoryWord);
    }

    /**
     * {@code DELETE  /category-words/:id} : delete the "id" categoryWord.
     *
     * @param id the id of the categoryWord to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/category-words/{id}")
    public ResponseEntity<Void> deleteCategoryWord(@PathVariable Long id) {
        log.debug("REST request to delete CategoryWord : {}", id);
        categoryWordRepository.deleteById(id);
        categoryWordSearchRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/category-words?query=:query} : search for the categoryWord corresponding
     * to the query.
     *
     * @param query the query of the categoryWord search.
     * @return the result of the search.
     */
    @GetMapping("/_search/category-words")
    public List<CategoryWord> searchCategoryWords(@RequestParam String query) {
        log.debug("REST request to search CategoryWords for query {}", query);
        return StreamSupport
            .stream(categoryWordSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
