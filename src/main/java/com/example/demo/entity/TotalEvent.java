package com.example.demo.entity;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class TotalEvent {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eventDay;

    private LocalDate luckyDay;

    @Column(nullable = true)
    private int dailyQuota;




}
