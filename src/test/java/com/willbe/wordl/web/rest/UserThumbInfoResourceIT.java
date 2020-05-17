package com.willbe.wordl.web.rest;

import com.willbe.wordl.WordlearnbackendApp;
import com.willbe.wordl.domain.UserThumbInfo;
import com.willbe.wordl.repository.UserThumbInfoRepository;
import com.willbe.wordl.repository.search.UserThumbInfoSearchRepository;
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
 * Integration tests for the {@link UserThumbInfoResource} REST controller.
 */
@SpringBootTest(classes = WordlearnbackendApp.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
public class UserThumbInfoResourceIT {

    private static final String DEFAULT_WORD = "AAAAAAAAAA";
    private static final String UPDATED_WORD = "BBBBBBBBBB";

    private static final Integer DEFAULT_SELF_NUM = 1;
    private static final Integer UPDATED_SELF_NUM = 2;

    private static final Integer DEFAULT_THUMB_NUM = 1;
    private static final Integer UPDATED_THUMB_NUM = 2;

    private static final String DEFAULT_PIC_URL = "AAAAAAAAAA";
    private static final String UPDATED_PIC_URL = "BBBBBBBBBB";

    private static final String DEFAULT_THUMB_LID = "AAAAAAAAAA";
    private static final String UPDATED_THUMB_LID = "BBBBBBBBBB";

    @Autowired
    private UserThumbInfoRepository userThumbInfoRepository;

    /**
     * This repository is mocked in the com.willbe.wordl.repository.search test package.
     *
     * @see com.willbe.wordl.repository.search.UserThumbInfoSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserThumbInfoSearchRepository mockUserThumbInfoSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserThumbInfoMockMvc;

    private UserThumbInfo userThumbInfo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserThumbInfo createEntity(EntityManager em) {
        UserThumbInfo userThumbInfo = new UserThumbInfo()
            .word(DEFAULT_WORD)
            .selfNum(DEFAULT_SELF_NUM)
            .thumbNum(DEFAULT_THUMB_NUM)
            .picUrl(DEFAULT_PIC_URL)
            .thumbLid(DEFAULT_THUMB_LID);
        return userThumbInfo;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserThumbInfo createUpdatedEntity(EntityManager em) {
        UserThumbInfo userThumbInfo = new UserThumbInfo()
            .word(UPDATED_WORD)
            .selfNum(UPDATED_SELF_NUM)
            .thumbNum(UPDATED_THUMB_NUM)
            .picUrl(UPDATED_PIC_URL)
            .thumbLid(UPDATED_THUMB_LID);
        return userThumbInfo;
    }

    @BeforeEach
    public void initTest() {
        userThumbInfo = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserThumbInfo() throws Exception {
        int databaseSizeBeforeCreate = userThumbInfoRepository.findAll().size();

        // Create the UserThumbInfo
        restUserThumbInfoMockMvc.perform(post("/api/user-thumb-infos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userThumbInfo)))
            .andExpect(status().isCreated());

        // Validate the UserThumbInfo in the database
        List<UserThumbInfo> userThumbInfoList = userThumbInfoRepository.findAll();
        assertThat(userThumbInfoList).hasSize(databaseSizeBeforeCreate + 1);
        UserThumbInfo testUserThumbInfo = userThumbInfoList.get(userThumbInfoList.size() - 1);
        assertThat(testUserThumbInfo.getWord()).isEqualTo(DEFAULT_WORD);
        assertThat(testUserThumbInfo.getSelfNum()).isEqualTo(DEFAULT_SELF_NUM);
        assertThat(testUserThumbInfo.getThumbNum()).isEqualTo(DEFAULT_THUMB_NUM);
        assertThat(testUserThumbInfo.getPicUrl()).isEqualTo(DEFAULT_PIC_URL);
        assertThat(testUserThumbInfo.getThumbLid()).isEqualTo(DEFAULT_THUMB_LID);

        // Validate the UserThumbInfo in Elasticsearch
        verify(mockUserThumbInfoSearchRepository, times(1)).save(testUserThumbInfo);
    }

    @Test
    @Transactional
    public void createUserThumbInfoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userThumbInfoRepository.findAll().size();

        // Create the UserThumbInfo with an existing ID
        userThumbInfo.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserThumbInfoMockMvc.perform(post("/api/user-thumb-infos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userThumbInfo)))
            .andExpect(status().isBadRequest());

        // Validate the UserThumbInfo in the database
        List<UserThumbInfo> userThumbInfoList = userThumbInfoRepository.findAll();
        assertThat(userThumbInfoList).hasSize(databaseSizeBeforeCreate);

        // Validate the UserThumbInfo in Elasticsearch
        verify(mockUserThumbInfoSearchRepository, times(0)).save(userThumbInfo);
    }


    @Test
    @Transactional
    public void getAllUserThumbInfos() throws Exception {
        // Initialize the database
        userThumbInfoRepository.saveAndFlush(userThumbInfo);

        // Get all the userThumbInfoList
        restUserThumbInfoMockMvc.perform(get("/api/user-thumb-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userThumbInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].word").value(hasItem(DEFAULT_WORD)))
            .andExpect(jsonPath("$.[*].selfNum").value(hasItem(DEFAULT_SELF_NUM)))
            .andExpect(jsonPath("$.[*].thumbNum").value(hasItem(DEFAULT_THUMB_NUM)))
            .andExpect(jsonPath("$.[*].picUrl").value(hasItem(DEFAULT_PIC_URL)))
            .andExpect(jsonPath("$.[*].thumbLid").value(hasItem(DEFAULT_THUMB_LID)));
    }

    @Test
    @Transactional
    public void getUserThumbInfo() throws Exception {
        // Initialize the database
        userThumbInfoRepository.saveAndFlush(userThumbInfo);

        // Get the userThumbInfo
        restUserThumbInfoMockMvc.perform(get("/api/user-thumb-infos/{id}", userThumbInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userThumbInfo.getId().intValue()))
            .andExpect(jsonPath("$.word").value(DEFAULT_WORD))
            .andExpect(jsonPath("$.selfNum").value(DEFAULT_SELF_NUM))
            .andExpect(jsonPath("$.thumbNum").value(DEFAULT_THUMB_NUM))
            .andExpect(jsonPath("$.picUrl").value(DEFAULT_PIC_URL))
            .andExpect(jsonPath("$.thumbLid").value(DEFAULT_THUMB_LID));
    }

    @Test
    @Transactional
    public void getNonExistingUserThumbInfo() throws Exception {
        // Get the userThumbInfo
        restUserThumbInfoMockMvc.perform(get("/api/user-thumb-infos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserThumbInfo() throws Exception {
        // Initialize the database
        userThumbInfoRepository.saveAndFlush(userThumbInfo);

        int databaseSizeBeforeUpdate = userThumbInfoRepository.findAll().size();

        // Update the userThumbInfo
        UserThumbInfo updatedUserThumbInfo = userThumbInfoRepository.findById(userThumbInfo.getId()).get();
        // Disconnect from session so that the updates on updatedUserThumbInfo are not directly saved in db
        em.detach(updatedUserThumbInfo);
        updatedUserThumbInfo
            .word(UPDATED_WORD)
            .selfNum(UPDATED_SELF_NUM)
            .thumbNum(UPDATED_THUMB_NUM)
            .picUrl(UPDATED_PIC_URL)
            .thumbLid(UPDATED_THUMB_LID);

        restUserThumbInfoMockMvc.perform(put("/api/user-thumb-infos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedUserThumbInfo)))
            .andExpect(status().isOk());

        // Validate the UserThumbInfo in the database
        List<UserThumbInfo> userThumbInfoList = userThumbInfoRepository.findAll();
        assertThat(userThumbInfoList).hasSize(databaseSizeBeforeUpdate);
        UserThumbInfo testUserThumbInfo = userThumbInfoList.get(userThumbInfoList.size() - 1);
        assertThat(testUserThumbInfo.getWord()).isEqualTo(UPDATED_WORD);
        assertThat(testUserThumbInfo.getSelfNum()).isEqualTo(UPDATED_SELF_NUM);
        assertThat(testUserThumbInfo.getThumbNum()).isEqualTo(UPDATED_THUMB_NUM);
        assertThat(testUserThumbInfo.getPicUrl()).isEqualTo(UPDATED_PIC_URL);
        assertThat(testUserThumbInfo.getThumbLid()).isEqualTo(UPDATED_THUMB_LID);

        // Validate the UserThumbInfo in Elasticsearch
        verify(mockUserThumbInfoSearchRepository, times(1)).save(testUserThumbInfo);
    }

    @Test
    @Transactional
    public void updateNonExistingUserThumbInfo() throws Exception {
        int databaseSizeBeforeUpdate = userThumbInfoRepository.findAll().size();

        // Create the UserThumbInfo

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserThumbInfoMockMvc.perform(put("/api/user-thumb-infos").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userThumbInfo)))
            .andExpect(status().isBadRequest());

        // Validate the UserThumbInfo in the database
        List<UserThumbInfo> userThumbInfoList = userThumbInfoRepository.findAll();
        assertThat(userThumbInfoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserThumbInfo in Elasticsearch
        verify(mockUserThumbInfoSearchRepository, times(0)).save(userThumbInfo);
    }

    @Test
    @Transactional
    public void deleteUserThumbInfo() throws Exception {
        // Initialize the database
        userThumbInfoRepository.saveAndFlush(userThumbInfo);

        int databaseSizeBeforeDelete = userThumbInfoRepository.findAll().size();

        // Delete the userThumbInfo
        restUserThumbInfoMockMvc.perform(delete("/api/user-thumb-infos/{id}", userThumbInfo.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserThumbInfo> userThumbInfoList = userThumbInfoRepository.findAll();
        assertThat(userThumbInfoList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UserThumbInfo in Elasticsearch
        verify(mockUserThumbInfoSearchRepository, times(1)).deleteById(userThumbInfo.getId());
    }

    @Test
    @Transactional
    public void searchUserThumbInfo() throws Exception {
        // Initialize the database
        userThumbInfoRepository.saveAndFlush(userThumbInfo);
        when(mockUserThumbInfoSearchRepository.search(queryStringQuery("id:" + userThumbInfo.getId())))
            .thenReturn(Collections.singletonList(userThumbInfo));
        // Search the userThumbInfo
        restUserThumbInfoMockMvc.perform(get("/api/_search/user-thumb-infos?query=id:" + userThumbInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userThumbInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].word").value(hasItem(DEFAULT_WORD)))
            .andExpect(jsonPath("$.[*].selfNum").value(hasItem(DEFAULT_SELF_NUM)))
            .andExpect(jsonPath("$.[*].thumbNum").value(hasItem(DEFAULT_THUMB_NUM)))
            .andExpect(jsonPath("$.[*].picUrl").value(hasItem(DEFAULT_PIC_URL)))
            .andExpect(jsonPath("$.[*].thumbLid").value(hasItem(DEFAULT_THUMB_LID)));
    }
}
