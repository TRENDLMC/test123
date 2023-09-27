package com.example.demo.repository;

import com.example.demo.entity.DailyLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DailyLimitRepository extends JpaRepository<DailyLimit, Integer> {


    @Query(value = "select sum(first) from daily_limit where everyday between 1 and :number",nativeQuery = true)
    public int totallimitfirstcheck(@Param("number")int number);
    @Query(value = "select sum(second) from daily_limit where everyday between 1 and :number",nativeQuery = true)
    public int totallimitsecondcheck(@Param("number")int number);
    @Query(value = "select sum(third) from daily_limit where everyday between 1 and :number",nativeQuery = true)
    public int totallimitthirdcheck(@Param("number")int number);
}
