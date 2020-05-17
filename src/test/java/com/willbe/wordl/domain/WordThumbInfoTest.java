package com.willbe.wordl.domain;

import com.willbe.wordl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WordThumbInfoTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(WordThumbInfo.class);
        WordThumbInfo wordThumbInfo1 = new WordThumbInfo();
        wordThumbInfo1.setId(1L);
        WordThumbInfo wordThumbInfo2 = new WordThumbInfo();
        wordThumbInfo2.setId(wordThumbInfo1.getId());
        assertThat(wordThumbInfo1).isEqualTo(wordThumbInfo2);
        wordThumbInfo2.setId(2L);
        assertThat(wordThumbInfo1).isNotEqualTo(wordThumbInfo2);
        wordThumbInfo1.setId(null);
        assertThat(wordThumbInfo1).isNotEqualTo(wordThumbInfo2);
    }
}
