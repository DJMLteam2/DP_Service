package com.dp.travel.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dp.travel.data.dto.TagDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TRAVEL_TAG_LIST")
@Getter
public class Tag {
    @Id
    @Column(name = "SPOT_TAG_ID")
    private Long TagID;

    @Column(name = "SPOT_TAG_NAME")
    private String TagName;

    @Column(name = "SPOT_TAG_THEME")
    private String TagTheme;


    public static TagDTO convertTagDTO(Tag tag){
        TagDTO tagDTO = new TagDTO();
        tagDTO.setTagID(tag.getTagID());
        tagDTO.setTagName(tag.getTagName());
        tagDTO.setTagTheme(tag.getTagTheme());
        return tagDTO;
    }
}
