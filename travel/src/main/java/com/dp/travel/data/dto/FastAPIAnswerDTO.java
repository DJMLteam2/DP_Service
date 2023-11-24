package com.dp.travel.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FastAPIAnswerDTO {
    private String title;
    private String area;
    private String overView;
    private String detail;
    private String lat;
    private String lon;
    private String similarity;
}
