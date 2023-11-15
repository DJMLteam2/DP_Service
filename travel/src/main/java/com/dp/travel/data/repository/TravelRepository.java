package com.dp.travel.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dp.travel.data.entity.Travel;

// import java.util.ArrayList;
import java.util.List;

public interface TravelRepository extends JpaRepository<Travel, Long> {
    @Query("SELECT t FROM Travel t WHERE t.location like %:every% or t.latitude like %:every% or t.longitude like %:every%")
    List<Travel> queryByEvery(String every);

    // @Override
    // ArrayList<Travel> findAll();
}
