package com.dp.travel.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.dp.travel.data.entity.Preferences;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreferencesDto {
    /*
     * Preferences의 Entity의 데이터를 끌고 오는 클래스
     * motivate: 여행동기
     * style_*: 여행스타일(1~8)
     */
    private long id;
    private String motivate;
    private int style_1;
    private int style_2;
    private int style_3;
    private int style_4;
    private int style_5;
    private int style_6;
    private int style_7;
    private int style_8;

    public Preferences toEntity(){
        return new Preferences(id, motivate, style_1, style_2, style_3, style_4, style_5, style_6, style_7, style_8);
    }

}
