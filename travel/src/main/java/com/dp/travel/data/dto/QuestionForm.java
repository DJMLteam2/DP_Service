package com.dp.travel.data.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QuestionForm {
    private String question;
    private String area;
}