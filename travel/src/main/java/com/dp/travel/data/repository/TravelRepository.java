package main.java.com.dp.travel.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import main.java.com.dp.travel.data.entity.Travel;

import java.util.ArrayList;
import java.util.List;

public interface TravelRepository extends JpaRepository<Travel, String> {
    @Query("SELECT t FROM Travel t WHERE t.name like %:every% or t.location like %:every%")
    List<Travel> queryByEvery(String every);

    @Override
    ArrayList<Travel> findAll();
}
