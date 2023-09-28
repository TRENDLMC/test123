package com.example.demo.repository;

import com.example.demo.entity.TotalEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TotalEventRepository extends JpaRepository<TotalEvent, Integer> {

    @Query(value = "select sum(daily_quota) from total_event where lucky_day between '20231215' and :time",nativeQuery = true)
    public Optional<Integer> selectTotal_Peple(@Param("time") String time);

}
