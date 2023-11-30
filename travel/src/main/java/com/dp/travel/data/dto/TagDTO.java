package com.dp.travel.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TagDTO {
    private Long TagID;
    private String TagName;
    private String TagTheme;
}
