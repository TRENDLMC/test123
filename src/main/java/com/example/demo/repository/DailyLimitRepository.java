package com.example.demo.repository;

import com.example.demo.entity.DailyLimit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyLimitRepository extends JpaRepository<DailyLimit, Integer> {
}
