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
    private int eventDay;//관계 및 db검색을 위한 int 타입의 pk키칼럼

    private LocalDate luckyDay;//이벤트일자 pk에따른 date값을 저장 하는 칼럼.

    @Column(nullable = true)
    private int dailyQuota;// 일별 총 참여자수를 저장하는 칼럼.

    @Column(nullable = true)
    private int firstcut;//당첨자번호 범위칼럼. 테스트용 null허용

    @Column(nullable = true)
    private int firstpe;//당첨자의 1등 당첨 퍼센트 칼럼. 테스트용 null허용

    @Column(nullable = true)
    private int secondcut;

    @Column(nullable = true)
    private int secondpe;

    @Column(nullable = true)
    private int thirdcut;

    @Column(nullable = true)
    private int thirdpe;

}
