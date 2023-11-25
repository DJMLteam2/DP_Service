package com.dp.travel.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dp.travel.data.entity.TravelSpotEntity;

@Repository
public interface TravelSpotRepository extends JpaRepository<TravelSpotEntity, Long> {
    @Query("SELECT c FROM TravelSpotEntity c WHERE c.spotTitle = :spotTitle")
    List<TravelSpotEntity> findTitle(@Param("spotTitle") String spotTitle);
}