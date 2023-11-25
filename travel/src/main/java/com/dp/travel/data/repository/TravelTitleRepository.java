package com.dp.travel.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dp.travel.data.entity.Travel;

public interface TravelTitleRepository extends JpaRepository<Travel, String>{
    @Query("Select t FROM Travel t where t.SPOT_TITLE =: SpotTitle")
    Travel queryBySpotTitle(String SpotTitle);  
} 