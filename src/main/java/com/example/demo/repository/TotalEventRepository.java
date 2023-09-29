package com.example.demo.repository;

import com.example.demo.entity.TotalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TotalEventRepository extends JpaRepository<TotalEvent, Integer> {

    @Query(value = "select sum(daily_quota) from total_event where lucky_day between '20230929' and :time",nativeQuery = true)
    public Optional<Integer> selectTotal_Peple(@Param("time") String time);

}
