package com.example.demo.repository;

import com.example.demo.entity.DailyEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface DailyEventRepository extends JpaRepository<DailyEvent,String> {


    @Query(value = "select count(*) from daily_event where luckyday=:time and ranks='first'", nativeQuery = true)
    public int firstcheck(@Param("time") String time);
    @Query(value = "select count(*) from daily_event where luckyday=:time and ranks='second'", nativeQuery = true)
    public int secondcheck(@Param("time") String time);
    @Query(value = "select count(*) from daily_event where luckyday=:time and ranks='third'", nativeQuery = true)
    public int thirdcheck(@Param("time") String time);

    @Query(value = "select count(*) from daily_event de where luckyday between '20231215' and :time and ranks='first'",nativeQuery = true)
    public int totalfirstcheck(@Param("time")String time);
    @Query(value = "select count(*) from daily_event de where luckyday between '20231215' and :time and ranks='second'",nativeQuery = true)
    public int totalsecondcheck(@Param("time")String time);
    @Query(value = "select count(*) from daily_event de where luckyday between '20231215' and :time and ranks='third'",nativeQuery = true)
    public int totalthirdcheck(@Param("time")String time);

}
