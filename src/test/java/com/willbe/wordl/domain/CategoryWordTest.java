package com.willbe.wordl.domain;

import com.willbe.wordl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CategoryWordTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CategoryWord.class);
        CategoryWord categoryWord1 = new CategoryWord();
        categoryWord1.setId(1L);
        CategoryWord categoryWord2 = new CategoryWord();
        categoryWord2.setId(categoryWord1.getId());
        assertThat(categoryWord1).isEqualTo(categoryWord2);
        categoryWord2.setId(2L);
        assertThat(categoryWord1).isNotEqualTo(categoryWord2);
        categoryWord1.setId(null);
        assertThat(categoryWord1).isNotEqualTo(categoryWord2);
    }
}
