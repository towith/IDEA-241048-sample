package com.willbe.wordl.web.rest;

import com.willbe.wordl.WordlearnbackendApp;
import com.willbe.wordl.domain.WordThumbInfo;
import com.willbe.wordl.repository.WordThumbInfoRepository;
import com.willbe.wordl.repository.search.WordThumbInfoSearchRepository;
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
 * Integration tests for the {@link WordThumbInfoResource} REST controller.
 */
@SpringBootTest(classes = WordlearnbackendApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class WordThumbInfoResourceIT {

    private static final Integer DEFAULT_THUMB_NUM = 1;
    private static final Integer UPDATED_THUMB_NUM = 2;

    private static final String DEFAULT_PIC_URL = "AAAAAAAAAA";
    private static final String UPDATED_PIC_URL = "BBBBBBBBBB";

    private static final String DEFAULT_THUMB_LID = "AAAAAAAAAA";
    private static final String UPDATED_THUMB_LID = "BBBBBBBBBB";

    @Autowired
    private WordThumbInfoRepository wordThumbInfoRepository;

    /**
     * This repository is mocked in the com.willbe.wordl.repository.search test package.
     *
     * @see com.willbe.wordl.repository.search.WordThumbInfoSearchRepositoryMockConfiguration
     */
    @Autowired
    private WordThumbInfoSearchRepository mockWordThumbInfoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWordThumbInfoMockMvc;

    private WordThumbInfo wordThumbInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WordThumbInfo createEntity(EntityManager em) {
        WordThumbInfo wordThumbInfo = new WordThumbInfo()
            .thumbNum(DEFAULT_THUMB_NUM)
            .picUrl(DEFAULT_PIC_URL)
            .thumbLid(DEFAULT_THUMB_LID);
        return wordThumbInfo;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WordThumbInfo createUpdatedEntity(EntityManager em) {
        WordThumbInfo wordThumbInfo = new WordThumbInfo()
            .thumbNum(UPDATED_THUMB_NUM)
            .picUrl(UPDATED_PIC_URL)
            .thumbLid(UPDATED_THUMB_LID);
        return wordThumbInfo;
    }

    @BeforeEach
    public void initTest() {
        wordThumbInfo = createEntity(em);
    }

    @Test
    @Transactional
    public void createWordThumbInfo() throws Exception {
        int databaseSizeBeforeCreate = wordThumbInfoRepository.findAll().size();

        // Create the WordThumbInfo
        restWordThumbInfoMockMvc.perform(post("/api/word-thumb-infos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(wordThumbInfo)))
            .andExpect(status().isCreated());

        // Validate the WordThumbInfo in the database
        List<WordThumbInfo> wordThumbInfoList = wordThumbInfoRepository.findAll();
        assertThat(wordThumbInfoList).hasSize(databaseSizeBeforeCreate + 1);
        WordThumbInfo testWordThumbInfo = wordThumbInfoList.get(wordThumbInfoList.size() - 1);
        assertThat(testWordThumbInfo.getThumbNum()).isEqualTo(DEFAULT_THUMB_NUM);
        assertThat(testWordThumbInfo.getPicUrl()).isEqualTo(DEFAULT_PIC_URL);
        assertThat(testWordThumbInfo.getThumbLid()).isEqualTo(DEFAULT_THUMB_LID);

        // Validate the WordThumbInfo in Elasticsearch
        verify(mockWordThumbInfoSearchRepository, times(1)).save(testWordThumbInfo);
    }

    @Test
    @Transactional
    public void createWordThumbInfoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = wordThumbInfoRepository.findAll().size();

        // Create the WordThumbInfo with an existing ID
        wordThumbInfo.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restWordThumbInfoMockMvc.perform(post("/api/word-thumb-infos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(wordThumbInfo)))
            .andExpect(status().isBadRequest());

        // Validate the WordThumbInfo in the database
        List<WordThumbInfo> wordThumbInfoList = wordThumbInfoRepository.findAll();
        assertThat(wordThumbInfoList).hasSize(databaseSizeBeforeCreate);

        // Validate the WordThumbInfo in Elasticsearch
        verify(mockWordThumbInfoSearchRepository, times(0)).save(wordThumbInfo);
    }


    @Test
    @Transactional
    public void getAllWordThumbInfos() throws Exception {
        // Initialize the database
        wordThumbInfoRepository.saveAndFlush(wordThumbInfo);

        // Get all the wordThumbInfoList
        restWordThumbInfoMockMvc.perform(get("/api/word-thumb-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wordThumbInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].thumbNum").value(hasItem(DEFAULT_THUMB_NUM)))
            .andExpect(jsonPath("$.[*].picUrl").value(hasItem(DEFAULT_PIC_URL)))
            .andExpect(jsonPath("$.[*].thumbLid").value(hasItem(DEFAULT_THUMB_LID)));
    }

    @Test
    @Transactional
    public void getWordThumbInfo() throws Exception {
        // Initialize the database
        wordThumbInfoRepository.saveAndFlush(wordThumbInfo);

        // Get the wordThumbInfo
        restWordThumbInfoMockMvc.perform(get("/api/word-thumb-infos/{id}", wordThumbInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wordThumbInfo.getId().intValue()))
            .andExpect(jsonPath("$.thumbNum").value(DEFAULT_THUMB_NUM))
            .andExpect(jsonPath("$.picUrl").value(DEFAULT_PIC_URL))
            .andExpect(jsonPath("$.thumbLid").value(DEFAULT_THUMB_LID));
    }

    @Test
    @Transactional
    public void getNonExistingWordThumbInfo() throws Exception {
        // Get the wordThumbInfo
        restWordThumbInfoMockMvc.perform(get("/api/word-thumb-infos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWordThumbInfo() throws Exception {
        // Initialize the database
        wordThumbInfoRepository.saveAndFlush(wordThumbInfo);

        int databaseSizeBeforeUpdate = wordThumbInfoRepository.findAll().size();

        // Update the wordThumbInfo
        WordThumbInfo updatedWordThumbInfo = wordThumbInfoRepository.findById(wordThumbInfo.getId()).get();
        // Disconnect from session so that the updates on updatedWordThumbInfo are not directly saved in db
        em.detach(updatedWordThumbInfo);
        updatedWordThumbInfo
            .thumbNum(UPDATED_THUMB_NUM)
            .picUrl(UPDATED_PIC_URL)
            .thumbLid(UPDATED_THUMB_LID);

        restWordThumbInfoMockMvc.perform(put("/api/word-thumb-infos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedWordThumbInfo)))
            .andExpect(status().isOk());

        // Validate the WordThumbInfo in the database
        List<WordThumbInfo> wordThumbInfoList = wordThumbInfoRepository.findAll();
        assertThat(wordThumbInfoList).hasSize(databaseSizeBeforeUpdate);
        WordThumbInfo testWordThumbInfo = wordThumbInfoList.get(wordThumbInfoList.size() - 1);
        assertThat(testWordThumbInfo.getThumbNum()).isEqualTo(UPDATED_THUMB_NUM);
        assertThat(testWordThumbInfo.getPicUrl()).isEqualTo(UPDATED_PIC_URL);
        assertThat(testWordThumbInfo.getThumbLid()).isEqualTo(UPDATED_THUMB_LID);

        // Validate the WordThumbInfo in Elasticsearch
        verify(mockWordThumbInfoSearchRepository, times(1)).save(testWordThumbInfo);
    }

    @Test
    @Transactional
    public void updateNonExistingWordThumbInfo() throws Exception {
        int databaseSizeBeforeUpdate = wordThumbInfoRepository.findAll().size();

        // Create the WordThumbInfo

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWordThumbInfoMockMvc.perform(put("/api/word-thumb-infos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(wordThumbInfo)))
            .andExpect(status().isBadRequest());

        // Validate the WordThumbInfo in the database
        List<WordThumbInfo> wordThumbInfoList = wordThumbInfoRepository.findAll();
        assertThat(wordThumbInfoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WordThumbInfo in Elasticsearch
        verify(mockWordThumbInfoSearchRepository, times(0)).save(wordThumbInfo);
    }

    @Test
    @Transactional
    public void deleteWordThumbInfo() throws Exception {
        // Initialize the database
        wordThumbInfoRepository.saveAndFlush(wordThumbInfo);

        int databaseSizeBeforeDelete = wordThumbInfoRepository.findAll().size();

        // Delete the wordThumbInfo
        restWordThumbInfoMockMvc.perform(delete("/api/word-thumb-infos/{id}", wordThumbInfo.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WordThumbInfo> wordThumbInfoList = wordThumbInfoRepository.findAll();
        assertThat(wordThumbInfoList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the WordThumbInfo in Elasticsearch
        verify(mockWordThumbInfoSearchRepository, times(1)).deleteById(wordThumbInfo.getId());
    }

    @Test
    @Transactional
    public void searchWordThumbInfo() throws Exception {
        // Initialize the database
        wordThumbInfoRepository.saveAndFlush(wordThumbInfo);
        when(mockWordThumbInfoSearchRepository.search(queryStringQuery("id:" + wordThumbInfo.getId())))
            .thenReturn(Collections.singletonList(wordThumbInfo));
        // Search the wordThumbInfo
        restWordThumbInfoMockMvc.perform(get("/api/_search/word-thumb-infos?query=id:" + wordThumbInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wordThumbInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].thumbNum").value(hasItem(DEFAULT_THUMB_NUM)))
            .andExpect(jsonPath("$.[*].picUrl").value(hasItem(DEFAULT_PIC_URL)))
            .andExpect(jsonPath("$.[*].thumbLid").value(hasItem(DEFAULT_THUMB_LID)));
    }
}
