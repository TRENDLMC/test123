package com.example.demo.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalEvent {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int eventday;

    @Temporal(TemporalType.DATE)
    private Date luckyday;

    private int dailyQuota;




}
