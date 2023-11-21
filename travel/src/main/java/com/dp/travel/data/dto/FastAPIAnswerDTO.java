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
    String title;
    String area;
    String overView;
    String detail;
    String lat;
    String lon;
    String similarity;
}
