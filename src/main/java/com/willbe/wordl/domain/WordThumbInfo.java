package com.willbe.wordl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A WordThumbInfo.
 */
@Entity
@Table(name = "word_thumb_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "wordthumbinfo")
public class WordThumbInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "thumb_num")
    private Integer thumbNum;

    @Column(name = "pic_url")
    private String picUrl;

    @Column(name = "thumb_lid")
    private String thumbLid;

    @ManyToOne
    @JsonIgnoreProperties("wordThumbInfos")
    private WordInfo word;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getThumbNum() {
        return thumbNum;
    }

    public WordThumbInfo thumbNum(Integer thumbNum) {
        this.thumbNum = thumbNum;
        return this;
    }

    public void setThumbNum(Integer thumbNum) {
        this.thumbNum = thumbNum;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public WordThumbInfo picUrl(String picUrl) {
        this.picUrl = picUrl;
        return this;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getThumbLid() {
        return thumbLid;
    }

    public WordThumbInfo thumbLid(String thumbLid) {
        this.thumbLid = thumbLid;
        return this;
    }

    public void setThumbLid(String thumbLid) {
        this.thumbLid = thumbLid;
    }

    public WordInfo getWord() {
        return word;
    }

    public WordThumbInfo word(WordInfo wordInfo) {
        this.word = wordInfo;
        return this;
    }

    public void setWord(WordInfo wordInfo) {
        this.word = wordInfo;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WordThumbInfo)) {
            return false;
        }
        return id != null && id.equals(((WordThumbInfo) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "WordThumbInfo{" +
            "id=" + getId() +
            ", thumbNum=" + getThumbNum() +
            ", picUrl='" + getPicUrl() + "'" +
            ", thumbLid='" + getThumbLid() + "'" +
            "}";
    }
}
