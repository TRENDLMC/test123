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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //당첨자의 유저아이디를 저장하는 칼럼.

    @ManyToOne
    @JoinColumn(name="eventDay", insertable = false, updatable = false)
    private TotalEvent totalEvent;//totalevent와 관계를 맺기위해서 설정해줌.

    private int eventDay;//관계시 사용될 fk키

    private LocalDate luckyDay;//유저의 당첨날짜를 저장할 date타입의 칼럼.

    private String ranks;//당첨등수를 저장할 String 칼럼.

}
