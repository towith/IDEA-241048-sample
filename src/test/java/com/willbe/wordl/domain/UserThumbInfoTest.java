package com.willbe.wordl.domain;

import com.willbe.wordl.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserThumbInfoTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserThumbInfo.class);
        UserThumbInfo userThumbInfo1 = new UserThumbInfo();
        userThumbInfo1.setId(1L);
        UserThumbInfo userThumbInfo2 = new UserThumbInfo();
        userThumbInfo2.setId(userThumbInfo1.getId());
        assertThat(userThumbInfo1).isEqualTo(userThumbInfo2);
        userThumbInfo2.setId(2L);
        assertThat(userThumbInfo1).isNotEqualTo(userThumbInfo2);
        userThumbInfo1.setId(null);
        assertThat(userThumbInfo1).isNotEqualTo(userThumbInfo2);
    }
}
