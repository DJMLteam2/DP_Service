package com.dp.travel.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.dp.travel.data.entity.Travel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TravelDto {
    private long id;
    private String location;
    private double latitude;
    private double longitude;
    //테이블 컬럼에 따라 추가 및 변경. 일단 기본으로 위치, 위도, 경도 
    private String radioOption; // 라디오 버튼 항목 추가


    public Travel toEntity() {
        return new Travel(id, location, latitude, longitude, radioOption);
    }
    

    // 라디오 버튼 항목 게터와 세터
    public String getRadioOption() {
        return radioOption;
    }

    public void setRadioOption(String radioOption) {
        this.radioOption = radioOption;
    }
}
