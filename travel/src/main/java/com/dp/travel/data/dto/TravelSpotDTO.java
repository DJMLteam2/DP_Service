package com.dp.travel.data.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class TravelSpotDTO {
    private Long spotId;
    private String spotCity;
    private int spotCityCode;
    private int spotCityContentType;
    private String spotTitle;
    private String spotCatchtitle;
    private String spotOverview;
    private String spotTreatmenu;
    private int spotConlike;
    private int spotConread;
    private int spotConshare;
    private String spotImgpath;
    private String spotAddr;
    private String spotInfocenter;
    private String spotParking;
    private String spotUsetime;
    private String spotTagname;
    private String spotDetail;
    private BigDecimal spotLon;
    private BigDecimal spotLat;
}
