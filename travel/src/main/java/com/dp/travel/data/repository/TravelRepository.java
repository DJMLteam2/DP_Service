package com.dp.travel.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dp.travel.data.entity.Travel;

public interface TravelRepository extends JpaRepository<Travel, Long>{

    // id 검색
    @Query("Select t FROM Travel t where t.SPOT_ID = :SpotId")
    Travel queryBySpotId(Long SpotId);  
    // title 검색
    @Query("Select t FROM Travel t where t.SPOT_TITLE =: SpotTitle")
    Travel queryBySpotTitle(String SpotTitle);  
    
} 