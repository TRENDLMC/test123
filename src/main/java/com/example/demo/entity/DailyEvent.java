package com.example.demo.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyEvent {


    @Id
    private String id;


    @ManyToOne
    @JoinColumn(name="eventday", insertable = false, updatable = false)
    private TotalEvent totalEvent;

    private int eventday;

    @Temporal(TemporalType.DATE)
    private Date luckyday;

    private String ranks;

}
