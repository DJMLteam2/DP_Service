package com.dp.travel.data.repository;

// import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dp.travel.data.entity.Travel;

public interface TravelRepository extends JpaRepository<Travel, Long> {
    @Query("SELECT t FROM Travel t WHERE t.location LIKE %:every% OR t.latitude LIKE %:every% OR t.longitude LIKE %:every%")
    List<Travel> queryByEvery(@Param("every") String every);
}
// @Override
// ArrayList<Travel> findAll();
