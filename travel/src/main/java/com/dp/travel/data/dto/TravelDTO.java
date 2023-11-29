package com.dp.travel.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TravelDTO {
    private Long spotId;
    private String spotCity;
    private int spotCityCode;
    private String spotCityContentType;
    private String spotTitle;
    private String spotCatchTitle;
    private String spotOverview;
    private String spotTreatMenu;
    private int spotConLike;
    private int spotConRead;
    private int spotConShare;
    private String spotImgPath;
    private String spotAddr;
    private String spotInfoCenter;
    private String spotParking;
    private String spotUseTime;
    private String spotTagName;
    private String spotDetail;
    private Double spotLon;
    private Double spotLat;
}