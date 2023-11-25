package com.dp.travel.data.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class FastAPIAnswerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SPOT_ID")
    private Long spotId;

    @Column(name = "SPOT_CITY")
    private String spotCity;

    @Column(name = "SPOT_CITY_CODE")
    private String spotCityCode;

    @Column(name = "SPOT_CITY_CONTENT_TYPE")
    private String spotCityContentType;

    @Column(name = "SPOT_TITLE")
    private String spotTitle;

    @Column(name = "SPOT_CATCHTITLE")
    private String spotCatchtitle;

    @Column(name = "SPOT_OVERVIEW")
    private String spotOverview;

    @Column(name = "SPOT_TREATMENU")
    private String spotTreatmenu;

    @Column(name = "SPOT_CONLIKE")
    private String spotConlike;

    @Column(name = "SPOT_CONREAD")
    private String spotConread;

    @Column(name = "SPOT_CONSHARE")
    private String spotConshare;

    @Column(name = "SPOT_IMGPATH")
    private String spotImgpath;

    @Column(name = "SPOT_ADDR")
    private String spotAddr;

    @Column(name = "SPOT_INFOCENTER")
    private String spotInfocenter;

    @Column(name = "SPOT_PARKING")
    private String spotParking;

    @Column(name = "SPOT_USETIME")
    private String spotUsetime;

    @Column(name = "SPOT_TAGNAME")
    private String spotTagname;

    @Column(name = "SPOT_DETAIL")
    private String spotDetail;

    @Column(name = "SPOT_LON")
    private BigDecimal spotLon;

    @Column(name = "SPOT_LAT")
    private BigDecimal spotLat;

}
