package com.dp.travel.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelDTO {
    private Long SPOT_ID;
    private String SPOT_CITY;
    private int SPOT_CITY_CODE;
    private String SPOT_CITY_CONTENT_TYPE;
    private String SPOT_TITLE;
    private String SPOT_CATCHTITLE;
    private String SPOT_OVERVIEW;
    private String SPOT_TREATMENU;
    private int SPOT_CONLIKE;
    private int SPOT_CONREAD;
    private int SPOT_CONSHARE;
    private String SPOT_IMGPATH;
    private String SPOT_ADDR;
    private String SPOT_INFOCENTER;
    private String SPOT_PARKING;
    private String SPOT_USETIME;
    private String SPOT_TAGNAME;
    private String SPOT_DETAIL;
    private Double SPOT_LON;
    private Double SPOT_LAT;
}