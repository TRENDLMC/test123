package com.example.demo.repository;

import com.example.demo.entity.DailyEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface DailyEventRepository extends JpaRepository<DailyEvent,Integer> {


    @Query(value = "select count(*) from daily_event where lucky_day=:time and ranks='1'", nativeQuery = true)
    public int selectFirst_Check(@Param("time") String time);
    @Query(value = "select count(*) from daily_event where lucky_day=:time and ranks='2'", nativeQuery = true)
    public int selectSecond_Check(@Param("time") String time);
    @Query(value = "select count(*) from daily_event where lucky_day=:time and ranks='3'", nativeQuery = true)
    public int selectThird_Check(@Param("time") String time);

    @Query(value = "select count(*) from daily_event de where lucky_day between '20230929' and :time and ranks='1'",nativeQuery = true)
    public int selectTotalFirst_Check(@Param("time")String time);
    @Query(value = "select count(*) from daily_event de where lucky_day between '20230929' and :time and ranks='2'",nativeQuery = true)
    public int selectTotalSecond_Check(@Param("time")String time);
    @Query(value = "select count(*) from daily_event de where lucky_day between '20230929' and :time and ranks='3'",nativeQuery = true)
    public int selectTotalThird_Check(@Param("time")String time);

    @Query(value="select count(*) from daily_event where lucky_day=:time",nativeQuery = true)
    public int total_Peple(@Param("time")String time);

}
