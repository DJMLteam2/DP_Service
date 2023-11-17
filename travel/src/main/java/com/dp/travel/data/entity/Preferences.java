package com.dp.travel.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class Preferences {
    /*
     * 여행목적 
     * 여행스타일1~8까지 점수 1~5점
     */
    @Id
    private long id;

    @Column(name = "Motivate")
    private String motivate;

    @Column(name="Style1")
    private int style_1;
    
    @Column(name="Style2")
    private int style_2;
    
    @Column(name="Style3")
    private int style_3;
    
    @Column(name="Style4")
    private int style_4;
    
    @Column(name="Style5")
    private int style_5;
    
    @Column(name="Style6")
    private int style_6;

    @Column(name="Style7")
    private int style_7;

    @Column(name="Style8")
    private int style_8;

}
