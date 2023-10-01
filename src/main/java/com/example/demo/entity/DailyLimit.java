package com.example.demo.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int everyDay;//pk 이벤트진행날짜의 리미트를 값을 저장하기위한 값 및 검색을위해 사용할 기준값 칼럼.

    private int first;//1등의 일별인원이 저장되는칼럼.

    private int second;//2등의 일별인원이 저장되는 칼럼.

    private int third;//3등의 일별인원이 저장되는 칼럼.


}
