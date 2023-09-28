package com.example.demo.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalEvent {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eventDay;

    private LocalDate luckyDay;

    @Column(nullable = true)
    private int dailyQuota;




}
