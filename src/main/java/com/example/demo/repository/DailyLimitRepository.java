package com.example.demo.repository;

import com.example.demo.entity.DailyLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailyLimitRepository extends JpaRepository<DailyLimit, Integer> {


    @Query(value = "select sum(first) from daily_limit where every_day between 1 and :number",nativeQuery = true)
    public int selectTotalFirst_Limit(@Param("number")int number);
    @Query(value = "select sum(second) from daily_limit where every_day between 1 and :number",nativeQuery = true)
    public int selectTotalSecond_Limit(@Param("number")int number);
    @Query(value = "select sum(third) from daily_limit where every_day between 1 and :number",nativeQuery = true)
    public int selectTotalThird_Limit(@Param("number")int number);
}
