package com.dp.travel.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dp.travel.data.entity.FastAPIAnswerEntity;

public interface FastAPIDataRepository extends JpaRepository<FastAPIAnswerEntity, Long> {
    
}
