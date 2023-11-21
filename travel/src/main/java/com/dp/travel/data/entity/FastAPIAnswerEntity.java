package com.dp.travel.data.entity;

import javax.persistence.Entity;

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
    String title;
    String area;
    String overView;
    String detail;
    String lat;
    String lon;
    String similarity;
}
