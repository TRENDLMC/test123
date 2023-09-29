package com.example.demo;

import com.example.demo.entity.DailyEvent;
import com.example.demo.entity.TotalEvent;
import com.example.demo.repository.DailyEventRepository;
import com.example.demo.repository.DailyLimitRepository;
import com.example.demo.repository.TotalEventRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class TestDemo {
    public int eventnumber=1;//이벤트진행일자.

    public int daily_Peple=720;//총당첨자수는 10800명이기떄문에 /15 을 해주어 첫날에만 예상값 720으로 값을 처리함.

    //오늘 혹은 현재당첨자수를 저장하기위한 변수생성
    public int first=0;
    public int second=0;

    public int third=0;

    //일별 1,2,3등의 당첨제한자를 위한 변수생성
    public int firstlimit=0;
    public int secondlimit=0;
    public int thirdlimit=0;

    //확률조정을 위한 변수생성
    public int firstcut=0;
    public int secondcut=0;
    public int thirdcut=0;

    private DailyEventRepository EventRepository;

    private DailyLimitRepository LimitRepository;


    private TotalEventRepository TotalRepository;

    public TestDemo(DailyEventRepository EventRepository, DailyLimitRepository LimitRepository, TotalEventRepository totalEventRepository){
     this.EventRepository=EventRepository;
     this.LimitRepository=LimitRepository;
     this.TotalRepository=totalEventRepository;
    }


    public void testrun()throws Exception{
        testdata();
        LocalDate currentDate = LocalDate.now();//이시간은 ex)17일 0시 0분이다
        for(int i=0; i<15; i++) {
            LocalDate notime=currentDate.plusDays(i);//i or 1
            int peple =(int) (Math.random()*1000) + 100;
            for (int j = 0; j < peple; j++) {
                asd(notime);
            }
            LocalDate notimes=currentDate.plusDays(i+1);//i or 1
            every(notimes);
        }
    }

    public void testdata(){
        LocalDate currentDate = LocalDate.now();// 현재시간을 기준으로 date타입생성.
        String time=currentDate.format(DateTimeFormatter.ofPattern("YYYYMMdd"));//Date타입을 String타입과 원하는형식으로 포맷시킴.
        rank_Check(time);//현재시간을 기준으로 오늘 당첨자수를 가져옴..
        limit_Check(time);//현재시간 기준으로 총당첨자-예상총당첨자 계산을하여 일별 당첨자제한을 가져옴.
        daily_Peple_Update(time);//현재시간 기준으로 총응모자를 가져온뒤 이벤트진행일자기준으로 나누어주어 평균일별 참여자를구함.
        change_Odds(); //위에서 일별평균방문자를 구한값으로 123등의 확률을 조정해줌.
    }

    public void  asd(LocalDate currentDate){
        int number = (int) (Math.random() * daily_Peple) + 1;

        if(number<=firstcut) {
            if(first<firstlimit) { //오늘당첨자와 일별당첨자의 수를 비교해서 초과시 2등으로 변경,
                first++;
                DailyEvent dailyEvent=DailyEvent.builder().eventDay(eventnumber).luckyDay(currentDate).ranks("1").build();
                EventRepository.save(dailyEvent);
                return;
            }
        }

        if(number<=secondcut) {
            if (second < secondlimit) {//오늘당첨자와 일별당첨자의 수를 비교해서 초과시 3등으로 변경,
                System.out.println("2등당첨");
                DailyEvent dailyEvent=DailyEvent.builder().eventDay(eventnumber).luckyDay(currentDate).ranks("2").build();
                EventRepository.save(dailyEvent);
                second++;
                return;
            }
        }

        if(number<=thirdcut){
            if(third < thirdlimit){//오늘당첨자와 일별당첨자의 수를 비교해서 초과시 4등으로 변경,
                System.out.println("3등당첨");
                DailyEvent dailyEvent=DailyEvent.builder().eventDay(eventnumber).luckyDay(currentDate).ranks("3").build();
                EventRepository.save(dailyEvent);
                third++;
                return;
            }
        }
        EventRepository.save(DailyEvent.builder().eventDay(eventnumber).luckyDay(currentDate).ranks("4").build());
        return;
    }

    public void every(LocalDate currentDate)throws Exception{//기준 17일 으로 주석을 작성함.
        LocalDate newDate = currentDate.minusDays(1);//이날짜는 ex)16일 0시 0분이다.
        String minus_Time=newDate.format(DateTimeFormatter.ofPattern("YYYYMMdd"));;//16일날짜를 String타입으로 변환시킴.

        Optional<TotalEvent> select_Data=TotalRepository.findById(eventnumber);



        if(select_Data.isPresent()){
            TotalEvent update_Data = select_Data.get();
            update_Data.setDailyQuota(EventRepository.total_Peple(minus_Time));
            TotalRepository.save(update_Data);//16일 데이터베이스를 업데이트해줌.
        }

        eventnumber++;//이벤트날짜를 증감시켜줌
        String now_Time=currentDate.format(DateTimeFormatter.ofPattern("YYYYMMdd"));//date타입을 String으로 타입변환
        rank_Check(now_Time);//오늘 날짜 ex)17일 기준으로 1~3등의 총당첨자수를 각각변수에 저장.
        limit_Check(now_Time);//오늘 날짜 ex)17일경우 17일기준으로 1~3등의 총당첨돼어야하는 예상값을 각각 변수에 저장해줌.

        TotalEvent new_Data= TotalEvent.builder().luckyDay(currentDate).build(); //17일의 tital2 객체생성.
        TotalRepository.save(new_Data);//객체를 데이터 베이스에 삽입.

        daily_Peple_Update(now_Time); //17일 기준으로 총참여자를 가져온후 이벤트진행일자 2로나누어서 평균일별 참여자를구함.
        change_Odds();//위에서 구한 평균값을 기준으로 123등의 확률을 변동해줌.



    }

    public void limit_Check(String time){
        //이벤트 진행일 기준로하여  총당첨자와 예상총당첨자의 수를 가져와서 차를 구해주고 전날 과락으로 인한 123등의 공백을 채우기위해 limit의 수를 증감시킴.
        // 전날 과락이 없다면 일별 당첨자를 그대로 유지함. 0 1 1 2 2 3 3 4 4 5
        firstlimit=LimitRepository.selectTotalFirst_Limit(eventnumber)-EventRepository.selectTotalFirst_Check(time);
        secondlimit=LimitRepository.selectTotalSecond_Limit(eventnumber)-EventRepository.selectTotalSecond_Check(time);
        thirdlimit=LimitRepository.selectTotalThird_Limit(eventnumber)-EventRepository.selectTotalThird_Check(time);
    }

    public void rank_Check(String time){
        //만약 서버가 비정상적으로 종료되었을시 다시 가져와주는 역활도함.
        //자바에 변수에 저장해두었던 일별당첨자를 초기화 시켜줌
        first=EventRepository.selectFirst_Check(time);
        second=EventRepository.selectSecond_Check(time);
        third=EventRepository.selectThird_Check(time);
    }

    public void change_Odds(){
        //과락당첨자수와 일별참여자수를 계산하여 확률을 조정해줌.
        double every_Peple=(double) daily_Peple /(double) 1000; //int 타입으로 나눗셈진행시 소수점숫자가 사라지기때문에
        //double타입으로 변경하여 값을 만들어서 설정해줌.
        firstcut = (int) Math.ceil(firstlimit / every_Peple);
        secondcut = (int) Math.ceil(secondlimit /every_Peple );
        thirdcut = (int) Math.ceil(thirdlimit /every_Peple );
    }

    public void daily_Peple_Update(String time){
        Optional<Integer> number=TotalRepository.selectTotal_Peple(time);//null값 체크를위해 optional타입으로 받아줌.

        if(number.isPresent()&& number.get()!=0) {//null값 체크
            daily_Peple =(int) number.get()/eventnumber;//null값 체크후 총참여인원/현재진행일자로 값을 나누어서 저장해줌.
            return;
        }
        daily_Peple=(int) daily_Peple/eventnumber;
    }

}
