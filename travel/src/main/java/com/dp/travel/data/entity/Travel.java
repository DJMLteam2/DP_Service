package com.dp.travel.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "TRAVEL_SPOT")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class Travel {
    @Id
    @Column(name = "SPOT_ID")
    private Long SPOT_ID;

    @Column(name = "SPOT_CITY")
    private String SPOT_CITY;

    @Column(name = "SPOT_CITY_CODE")
    private int SPOT_CITY_CODE;
    
    @Column(name = "SPOT_CITY_CONTENT_TYPE")
    private String SPOT_CITY_CONTENT_TYPE;
    
    @Column(name = "SPOT_TITLE")
    private String SPOT_TITLE;

    @Column(name = "SPOT_CATCHTITLE")
    private String SPOT_CATCHTITLE;

    @Column(name = "SPOT_OVERVIEW")
    private String SPOT_OVERVIEW;
    
    @Column(name = "SPOT_TREATMENU")
    private String SPOT_TREATMENU;

    @Column(name = "SPOT_CONLIKE")
    private int SPOT_CONLIKE;

    @Column(name = "SPOT_CONREAD")
    private int SPOT_CONREAD;
    
    @Column(name = "SPOT_CONSHARE")
    private int SPOT_CONSHARE;

    @Column(name = "SPOT_IMGPATH")
    private String SPOT_IMGPATH;
    
    @Column(name = "SPOT_ADDR")
    private String SPOT_ADDR;
    
    @Column(name = "SPOT_INFOCENTER")
    private String SPOT_INFOCENTER;
    
    @Column(name = "SPOT_PARKING")
    private String SPOT_PARKING;
    
    @Column(name = "SPOT_USETIME")
    private String SPOT_USETIME;

    @Column(name = "SPOT_TAGNAME")
    private String SPOT_TAGNAME;

    @Column(name = "SPOT_DETAIL")
    private String SPOT_DETAIL;

    @Column(name = "SPOT_LON")
    private Double SPOT_LON;

    @Column(name = "SPOT_LAT")
    private Double SPOT_LAT;
}
