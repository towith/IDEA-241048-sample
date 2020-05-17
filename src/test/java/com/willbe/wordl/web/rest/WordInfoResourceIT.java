package com.willbe.wordl.web.rest;

import com.willbe.wordl.WordlearnbackendApp;
import com.willbe.wordl.domain.WordInfo;
import com.willbe.wordl.repository.WordInfoRepository;
import com.willbe.wordl.repository.search.WordInfoSearchRepository;
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
 * Integration tests for the {@link WordInfoResource} REST controller.
 */
@SpringBootTest(classes = WordlearnbackendApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class WordInfoResourceIT {

    private static final String DEFAULT_WORD = "AAAAAAAAAA";
    private static final String UPDATED_WORD = "BBBBBBBBBB";

    @Autowired
    private WordInfoRepository wordInfoRepository;

    /**
     * This repository is mocked in the com.willbe.wordl.repository.search test package.
     *
     * @see com.willbe.wordl.repository.search.WordInfoSearchRepositoryMockConfiguration
     */
    @Autowired
    private WordInfoSearchRepository mockWordInfoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWordInfoMockMvc;

    private WordInfo wordInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WordInfo createEntity(EntityManager em) {
        WordInfo wordInfo = new WordInfo()
            .word(DEFAULT_WORD);
        return wordInfo;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WordInfo createUpdatedEntity(EntityManager em) {
        WordInfo wordInfo = new WordInfo()
            .word(UPDATED_WORD);
        return wordInfo;
    }

    @BeforeEach
    public void initTest() {
        wordInfo = createEntity(em);
    }

    @Test
    @Transactional
    public void createWordInfo() throws Exception {
        int databaseSizeBeforeCreate = wordInfoRepository.findAll().size();

        // Create the WordInfo
        restWordInfoMockMvc.perform(post("/api/word-infos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(wordInfo)))
            .andExpect(status().isCreated());

        // Validate the WordInfo in the database
        List<WordInfo> wordInfoList = wordInfoRepository.findAll();
        assertThat(wordInfoList).hasSize(databaseSizeBeforeCreate + 1);
        WordInfo testWordInfo = wordInfoList.get(wordInfoList.size() - 1);
        assertThat(testWordInfo.getWord()).isEqualTo(DEFAULT_WORD);

        // Validate the WordInfo in Elasticsearch
        verify(mockWordInfoSearchRepository, times(1)).save(testWordInfo);
    }

    @Test
    @Transactional
    public void createWordInfoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = wordInfoRepository.findAll().size();

        // Create the WordInfo with an existing ID
        wordInfo.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWordInfoMockMvc.perform(post("/api/word-infos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(wordInfo)))
            .andExpect(status().isBadRequest());

        // Validate the WordInfo in the database
        List<WordInfo> wordInfoList = wordInfoRepository.findAll();
        assertThat(wordInfoList).hasSize(databaseSizeBeforeCreate);

        // Validate the WordInfo in Elasticsearch
        verify(mockWordInfoSearchRepository, times(0)).save(wordInfo);
    }


    @Test
    @Transactional
    public void getAllWordInfos() throws Exception {
        // Initialize the database
        wordInfoRepository.saveAndFlush(wordInfo);

        // Get all the wordInfoList
        restWordInfoMockMvc.perform(get("/api/word-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wordInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].word").value(hasItem(DEFAULT_WORD)));
    }

    @Test
    @Transactional
    public void getWordInfo() throws Exception {
        // Initialize the database
        wordInfoRepository.saveAndFlush(wordInfo);

        // Get the wordInfo
        restWordInfoMockMvc.perform(get("/api/word-infos/{id}", wordInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wordInfo.getId().intValue()))
            .andExpect(jsonPath("$.word").value(DEFAULT_WORD));
    }

    @Test
    @Transactional
    public void getNonExistingWordInfo() throws Exception {
        // Get the wordInfo
        restWordInfoMockMvc.perform(get("/api/word-infos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWordInfo() throws Exception {
        // Initialize the database
        wordInfoRepository.saveAndFlush(wordInfo);

        int databaseSizeBeforeUpdate = wordInfoRepository.findAll().size();

        // Update the wordInfo
        WordInfo updatedWordInfo = wordInfoRepository.findById(wordInfo.getId()).get();
        // Disconnect from session so that the updates on updatedWordInfo are not directly saved in db
        em.detach(updatedWordInfo);
        updatedWordInfo
            .word(UPDATED_WORD);

        restWordInfoMockMvc.perform(put("/api/word-infos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedWordInfo)))
            .andExpect(status().isOk());

        // Validate the WordInfo in the database
        List<WordInfo> wordInfoList = wordInfoRepository.findAll();
        assertThat(wordInfoList).hasSize(databaseSizeBeforeUpdate);
        WordInfo testWordInfo = wordInfoList.get(wordInfoList.size() - 1);
        assertThat(testWordInfo.getWord()).isEqualTo(UPDATED_WORD);

        // Validate the WordInfo in Elasticsearch
        verify(mockWordInfoSearchRepository, times(1)).save(testWordInfo);
    }

    @Test
    @Transactional
    public void updateNonExistingWordInfo() throws Exception {
        int databaseSizeBeforeUpdate = wordInfoRepository.findAll().size();

        // Create the WordInfo

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWordInfoMockMvc.perform(put("/api/word-infos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(wordInfo)))
            .andExpect(status().isBadRequest());

        // Validate the WordInfo in the database
        List<WordInfo> wordInfoList = wordInfoRepository.findAll();
        assertThat(wordInfoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WordInfo in Elasticsearch
        verify(mockWordInfoSearchRepository, times(0)).save(wordInfo);
    }

    @Test
    @Transactional
    public void deleteWordInfo() throws Exception {
        // Initialize the database
        wordInfoRepository.saveAndFlush(wordInfo);

        int databaseSizeBeforeDelete = wordInfoRepository.findAll().size();

        // Delete the wordInfo
        restWordInfoMockMvc.perform(delete("/api/word-infos/{id}", wordInfo.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WordInfo> wordInfoList = wordInfoRepository.findAll();
        assertThat(wordInfoList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the WordInfo in Elasticsearch
        verify(mockWordInfoSearchRepository, times(1)).deleteById(wordInfo.getId());
    }

    @Test
    @Transactional
    public void searchWordInfo() throws Exception {
        // Initialize the database
        wordInfoRepository.saveAndFlush(wordInfo);
        when(mockWordInfoSearchRepository.search(queryStringQuery("id:" + wordInfo.getId())))
            .thenReturn(Collections.singletonList(wordInfo));
        // Search the wordInfo
        restWordInfoMockMvc.perform(get("/api/_search/word-infos?query=id:" + wordInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wordInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].word").value(hasItem(DEFAULT_WORD)));
    }
}
