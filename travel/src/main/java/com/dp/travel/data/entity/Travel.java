package com.dp.travel.data.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dp.travel.data.dto.TravelDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "TRAVEL_SPOT")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
public class Travel {
    @Id
    @Column(name = "SPOT_ID")
    private Long SPOT_ID;

    @Column(name = "SPOT_CITY")
    private String SPOT_CITY;

    @Column(name = "SPOT_CITY_CODE")
    private int SPOT_CITY_CODE;

    @Column(name = "SPOT_CITY_CONTENT_TYPE")
    private String SPOT_CITY_CONTENT_TYPE;

    @Column(name = "SPOT_TITLE")
    private String SPOT_TITLE;

    @Column(name = "SPOT_CATCHTITLE")
    private String SPOT_CATCHTITLE;

    @Column(name = "SPOT_OVERVIEW")
    private String SPOT_OVERVIEW;

    @Column(name = "SPOT_TREATMENU")
    private String SPOT_TREATMENU;

    @Column(name = "SPOT_CONLIKE")
    private int SPOT_CONLIKE;

    @Column(name = "SPOT_CONREAD")
    private int SPOT_CONREAD;

    @Column(name = "SPOT_CONSHARE")
    private int SPOT_CONSHARE;

    @Column(name = "SPOT_IMGPATH")
    private String SPOT_IMGPATH;

    @Column(name = "SPOT_ADDR")
    private String SPOT_ADDR;

    @Column(name = "SPOT_INFOCENTER")
    private String SPOT_INFOCENTER;

    @Column(name = "SPOT_PARKING")
    private String SPOT_PARKING;

    @Column(name = "SPOT_USETIME")
    private String SPOT_USETIME;

    @Column(name = "SPOT_TAGNAME")
    private String SPOT_TAGNAME;

    @Column(name = "SPOT_DETAIL")
    private String SPOT_DETAIL;

    @Column(name = "SPOT_LON")
    private Double SPOT_LON;

    @Column(name = "SPOT_LAT")
    private Double SPOT_LAT;


     // TravelDTO로 변환하는 메서드
    public TravelDTO toDTO(Travel travel) {
        TravelDTO dto = new TravelDTO();
        dto.setSpotId(this.SPOT_ID);
        dto.setSpotCity(this.SPOT_CITY);
        dto.setSpotCityCode(this.SPOT_CITY_CODE);
        dto.setSpotCityContentType(this.SPOT_CITY_CONTENT_TYPE);
        dto.setSpotTitle(this.SPOT_TITLE);
        dto.setSpotCatchTitle(this.SPOT_CATCHTITLE);
        dto.setSpotOverview(this.SPOT_OVERVIEW);
        dto.setSpotTreatMenu(this.SPOT_TREATMENU);
        dto.setSpotConLike(this.SPOT_CONLIKE);
        dto.setSpotConRead(this.SPOT_CONREAD);
        dto.setSpotConShare(this.SPOT_CONSHARE);
        dto.setSpotImgPath(this.SPOT_IMGPATH);
        dto.setSpotAddr(this.SPOT_ADDR);
        dto.setSpotInfoCenter(this.SPOT_INFOCENTER);
        dto.setSpotParking(this.SPOT_PARKING);
        dto.setSpotUseTime(this.SPOT_USETIME);
        dto.setSpotTagName(this.SPOT_TAGNAME);
        dto.setSpotDetail(this.SPOT_DETAIL);
        dto.setSpotLon(this.SPOT_LON);
        dto.setSpotLat(this.SPOT_LAT);
        return dto;
    }

    // DTO를 Travel로 변환하는 메서드
    public static Travel fromDTO(TravelDTO dto) {
        Travel entity = new Travel();
        entity.SPOT_ID = dto.getSpotId();
        entity.SPOT_CITY = dto.getSpotCity();
        entity.SPOT_CITY_CODE = dto.getSpotCityCode();
        entity.SPOT_CITY_CONTENT_TYPE = dto.getSpotCityContentType();
        entity.SPOT_TITLE = dto.getSpotTitle();
        entity.SPOT_CATCHTITLE = dto.getSpotCatchTitle();
        entity.SPOT_OVERVIEW = dto.getSpotOverview();
        entity.SPOT_TREATMENU = dto.getSpotTreatMenu();
        entity.SPOT_CONLIKE = dto.getSpotConLike();
        entity.SPOT_CONREAD = dto.getSpotConRead();
        entity.SPOT_CONSHARE = dto.getSpotConShare();
        entity.SPOT_IMGPATH = dto.getSpotImgPath();
        entity.SPOT_ADDR = dto.getSpotAddr();
        entity.SPOT_INFOCENTER = dto.getSpotInfoCenter();
        entity.SPOT_PARKING = dto.getSpotParking();
        entity.SPOT_USETIME = dto.getSpotUseTime();
        entity.SPOT_TAGNAME = dto.getSpotTagName();
        entity.SPOT_DETAIL = dto.getSpotDetail();
        entity.SPOT_LON = dto.getSpotLon();
        entity.SPOT_LAT = dto.getSpotLat();
        return entity;
    }

    public Object toDTO() {
        return null;
    }
}