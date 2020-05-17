package com.willbe.wordl.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A UserThumbInfo.
 */
@Entity
@Table(name = "user_thumb_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "userthumbinfo")
public class UserThumbInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "word")
    private String word;

    @Column(name = "self_num")
    private Integer selfNum;

    @Column(name = "thumb_num")
    private Integer thumbNum;

    @Column(name = "pic_url")
    private String picUrl;

    @Column(name = "thumb_lid")
    private String thumbLid;

    @ManyToOne
    @JsonIgnoreProperties("userThumbInfos")
    private User clicker;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public UserThumbInfo word(String word) {
        this.word = word;
        return this;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Integer getSelfNum() {
        return selfNum;
    }

    public UserThumbInfo selfNum(Integer selfNum) {
        this.selfNum = selfNum;
        return this;
    }

    public void setSelfNum(Integer selfNum) {
        this.selfNum = selfNum;
    }

    public Integer getThumbNum() {
        return thumbNum;
    }

    public UserThumbInfo thumbNum(Integer thumbNum) {
        this.thumbNum = thumbNum;
        return this;
    }

    public void setThumbNum(Integer thumbNum) {
        this.thumbNum = thumbNum;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public UserThumbInfo picUrl(String picUrl) {
        this.picUrl = picUrl;
        return this;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getThumbLid() {
        return thumbLid;
    }

    public UserThumbInfo thumbLid(String thumbLid) {
        this.thumbLid = thumbLid;
        return this;
    }

    public void setThumbLid(String thumbLid) {
        this.thumbLid = thumbLid;
    }

    public User getClicker() {
        return clicker;
    }

    public UserThumbInfo clicker(User user) {
        this.clicker = user;
        return this;
    }

    public void setClicker(User user) {
        this.clicker = user;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserThumbInfo)) {
            return false;
        }
        return id != null && id.equals(((UserThumbInfo) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "UserThumbInfo{" +
            "id=" + getId() +
            ", word='" + getWord() + "'" +
            ", selfNum=" + getSelfNum() +
            ", thumbNum=" + getThumbNum() +
            ", picUrl='" + getPicUrl() + "'" +
            ", thumbLid='" + getThumbLid() + "'" +
            "}";
    }
}
