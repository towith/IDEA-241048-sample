package com.willbe.wordl.web.rest;

import com.willbe.wordl.WordlearnbackendApp;
import com.willbe.wordl.domain.CategoryWord;
import com.willbe.wordl.repository.CategoryWordRepository;
import com.willbe.wordl.repository.search.CategoryWordSearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link CategoryWordResource} REST controller.
 */
@SpringBootTest(classes = WordlearnbackendApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class CategoryWordResourceIT {

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final String DEFAULT_WORD = "AAAAAAAAAA";
    private static final String UPDATED_WORD = "BBBBBBBBBB";

    @Autowired
    private CategoryWordRepository categoryWordRepository;

    /**
     * This repository is mocked in the com.willbe.wordl.repository.search test package.
     *
     * @see com.willbe.wordl.repository.search.CategoryWordSearchRepositoryMockConfiguration
     */
    @Autowired
    private CategoryWordSearchRepository mockCategoryWordSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCategoryWordMockMvc;

    private CategoryWord categoryWord;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CategoryWord createEntity(EntityManager em) {
        CategoryWord categoryWord = new CategoryWord()
            .category(DEFAULT_CATEGORY)
            .word(DEFAULT_WORD);
        return categoryWord;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CategoryWord createUpdatedEntity(EntityManager em) {
        CategoryWord categoryWord = new CategoryWord()
            .category(UPDATED_CATEGORY)
            .word(UPDATED_WORD);
        return categoryWord;
    }

    @BeforeEach
    public void initTest() {
        categoryWord = createEntity(em);
    }

    @Test
    @Transactional
    public void createCategoryWord() throws Exception {
        int databaseSizeBeforeCreate = categoryWordRepository.findAll().size();

        // Create the CategoryWord
        restCategoryWordMockMvc.perform(post("/api/category-words").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(categoryWord)))
            .andExpect(status().isCreated());

        // Validate the CategoryWord in the database
        List<CategoryWord> categoryWordList = categoryWordRepository.findAll();
        assertThat(categoryWordList).hasSize(databaseSizeBeforeCreate + 1);
        CategoryWord testCategoryWord = categoryWordList.get(categoryWordList.size() - 1);
        assertThat(testCategoryWord.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testCategoryWord.getWord()).isEqualTo(DEFAULT_WORD);

        // Validate the CategoryWord in Elasticsearch
        verify(mockCategoryWordSearchRepository, times(1)).save(testCategoryWord);
    }

    @Test
    @Transactional
    public void createCategoryWordWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = categoryWordRepository.findAll().size();

        // Create the CategoryWord with an existing ID
        categoryWord.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCategoryWordMockMvc.perform(post("/api/category-words").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(categoryWord)))
            .andExpect(status().isBadRequest());

        // Validate the CategoryWord in the database
        List<CategoryWord> categoryWordList = categoryWordRepository.findAll();
        assertThat(categoryWordList).hasSize(databaseSizeBeforeCreate);

        // Validate the CategoryWord in Elasticsearch
        verify(mockCategoryWordSearchRepository, times(0)).save(categoryWord);
    }


    @Test
    @Transactional
    public void getAllCategoryWords() throws Exception {
        // Initialize the database
        categoryWordRepository.saveAndFlush(categoryWord);

        // Get all the categoryWordList
        restCategoryWordMockMvc.perform(get("/api/category-words?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(categoryWord.getId().intValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY)))
            .andExpect(jsonPath("$.[*].word").value(hasItem(DEFAULT_WORD)));
    }

    @Test
    @Transactional
    public void getCategoryWord() throws Exception {
        // Initialize the database
        categoryWordRepository.saveAndFlush(categoryWord);

        // Get the categoryWord
        restCategoryWordMockMvc.perform(get("/api/category-words/{id}", categoryWord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(categoryWord.getId().intValue()))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY))
            .andExpect(jsonPath("$.word").value(DEFAULT_WORD));
    }

    @Test
    @Transactional
    public void getNonExistingCategoryWord() throws Exception {
        // Get the categoryWord
        restCategoryWordMockMvc.perform(get("/api/category-words/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCategoryWord() throws Exception {
        // Initialize the database
        categoryWordRepository.saveAndFlush(categoryWord);

        int databaseSizeBeforeUpdate = categoryWordRepository.findAll().size();

        // Update the categoryWord
        CategoryWord updatedCategoryWord = categoryWordRepository.findById(categoryWord.getId()).get();
        // Disconnect from session so that the updates on updatedCategoryWord are not directly saved in db
        em.detach(updatedCategoryWord);
        updatedCategoryWord
            .category(UPDATED_CATEGORY)
            .word(UPDATED_WORD);

        restCategoryWordMockMvc.perform(put("/api/category-words").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCategoryWord)))
            .andExpect(status().isOk());

        // Validate the CategoryWord in the database
        List<CategoryWord> categoryWordList = categoryWordRepository.findAll();
        assertThat(categoryWordList).hasSize(databaseSizeBeforeUpdate);
        CategoryWord testCategoryWord = categoryWordList.get(categoryWordList.size() - 1);
        assertThat(testCategoryWord.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testCategoryWord.getWord()).isEqualTo(UPDATED_WORD);

        // Validate the CategoryWord in Elasticsearch
        verify(mockCategoryWordSearchRepository, times(1)).save(testCategoryWord);
    }

    @Test
    @Transactional
    public void updateNonExistingCategoryWord() throws Exception {
        int databaseSizeBeforeUpdate = categoryWordRepository.findAll().size();

        // Create the CategoryWord

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCategoryWordMockMvc.perform(put("/api/category-words").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(categoryWord)))
            .andExpect(status().isBadRequest());

        // Validate the CategoryWord in the database
        List<CategoryWord> categoryWordList = categoryWordRepository.findAll();
        assertThat(categoryWordList).hasSize(databaseSizeBeforeUpdate);

        // Validate the CategoryWord in Elasticsearch
        verify(mockCategoryWordSearchRepository, times(0)).save(categoryWord);
    }

    @Test
    @Transactional
    public void deleteCategoryWord() throws Exception {
        // Initialize the database
        categoryWordRepository.saveAndFlush(categoryWord);

        int databaseSizeBeforeDelete = categoryWordRepository.findAll().size();

        // Delete the categoryWord
        restCategoryWordMockMvc.perform(delete("/api/category-words/{id}", categoryWord.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CategoryWord> categoryWordList = categoryWordRepository.findAll();
        assertThat(categoryWordList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the CategoryWord in Elasticsearch
        verify(mockCategoryWordSearchRepository, times(1)).deleteById(categoryWord.getId());
    }

    @Test
    @Transactional
    public void searchCategoryWord() throws Exception {
        // Initialize the database
        categoryWordRepository.saveAndFlush(categoryWord);
        when(mockCategoryWordSearchRepository.search(queryStringQuery("id:" + categoryWord.getId())))
            .thenReturn(Collections.singletonList(categoryWord));
        // Search the categoryWord
        restCategoryWordMockMvc.perform(get("/api/_search/category-words?query=id:" + categoryWord.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(categoryWord.getId().intValue())))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY)))
            .andExpect(jsonPath("$.[*].word").value(hasItem(DEFAULT_WORD)));
    }
}
