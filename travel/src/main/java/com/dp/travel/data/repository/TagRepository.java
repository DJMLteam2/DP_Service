package com.dp.travel.data.repository;

import com.dp.travel.data.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    // 랜덤 태그
    @Query
    (value = "SELECT * FROM TRAVEL_TAG_LIST ORDER BY RAND()", nativeQuery = true)
    List<Tag> queryRandomTags();
}

