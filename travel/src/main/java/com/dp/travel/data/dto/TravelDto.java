package main.java.com.dp.travel.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.minip.store.data.entity.Store;

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

    public Travel toEntity() {
        return new Travel(id, location, latitude, longitude);
    }
}
