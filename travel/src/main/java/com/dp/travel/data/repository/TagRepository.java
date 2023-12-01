package com.dp.travel.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dp.travel.data.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>{
    @Query("Select t FROM Tag t where t.TagID=:TagId")
    Tag ququeryByTag(@Param("TagId") Long TagId);
}