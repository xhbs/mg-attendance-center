package com.unisinsight.business.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * (AdsStudentBasicInformation)实体类
 *
 * @author XieHaiBo
 * @since 2023-03-29 15:35:46
 */
@Data
@Table(name = "ads_student_basic_information")
public class AdsStudentBasicInformation implements Serializable {

    private static final long serialVersionUID = 697786191606728986L;

    private String id;

    private String xh;

    private String xjh;

    private String xm;

    private String xb;

    private String csrq;

    private String xszp;

    private String nj;

    private String sfzjh;

    private String xz;

    private String xxjgmc;

    private String xxzgbmm;

    private String xxjgdzdm;

    private String zs;

    private String qx;

    private String bjmc;

    private String bh;

    private String zxszt;

    private String zxsztmc;

    private String zymc;

    private String updatetime;

    private String zt;

    private String image;

    @Column(name = "org_index")
    private String orgIndex;
    @Column(name = "xx_index")
    private String xxIndex;

}

