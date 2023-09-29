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

    @Column(nullable = true)
    private int firstcut;

    @Column(nullable = true)
    private int firstpe;

    @Column(nullable = true)
    private int secondcut;

    @Column(nullable = true)
    private int secondpe;

    @Column(nullable = true)
    private int thirdcut;

    @Column(nullable = true)
    private int thirdpe;

}
