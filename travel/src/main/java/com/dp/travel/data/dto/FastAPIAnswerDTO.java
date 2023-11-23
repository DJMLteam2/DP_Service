package com.dp.travel.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FastAPIAnswerDTO {

    private Long id;
    private String area;
    private String title;
    private float similarity;
    private String catchtitle;
    private String treatMenu;
    private String tagName;
    private String addr;
    private String info;
    private String useTime;
    private String conLike;
    private String conRead;
    private String conShare;
    private String lat;
    private String lon;
}