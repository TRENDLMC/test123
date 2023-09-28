package com.example.demo.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyEvent {


    @Id
    private String id;


    @ManyToOne
    @JoinColumn(name="eventDay", insertable = false, updatable = false)
    private TotalEvent totalEvent;

    private int eventDay;

    private LocalDate luckyDay;

    private String ranks;

}
