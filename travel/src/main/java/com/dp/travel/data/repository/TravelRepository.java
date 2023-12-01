package com.dp.travel.data.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.dp.travel.data.entity.Travel;

import java.util.List;

public interface TravelRepository extends JpaRepository<Travel, Long> {

    // id 검색
    @Query("Select t FROM Travel t where t.SPOT_ID=:SpotId")
    Travel queryBySpotId(@Param("SpotId") Long SpotId);

    // title 검색
    @Query("Select t FROM Travel t where t.SPOT_TITLE=:SpotTitle")
    Travel queryBySpotTitle(@Param("SpotTitle") String SpotTitle);

    @Query("SELECT DISTINCT t FROM Travel t WHERE t.SPOT_TAGNAME LIKE %:tag% ORDER BY t.SPOT_CONLIKE DESC")
    List<Travel> findtop5(@Param("tag") String tag, PageRequest pageable);
}