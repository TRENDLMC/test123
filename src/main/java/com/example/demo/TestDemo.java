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

    //확률조정의 범위를 지정할 변수생성
    public int firstcut=0;
    public int secondcut=0;
    public int thirdcut=0;


    // 범위를지정할 확률을 저장함.
    int first_pe=0;
    int second_pe=0;
    int third_pe=0;


    private DailyEventRepository EventRepository;

    private DailyLimitRepository LimitRepository;


    private TotalEventRepository TotalRepository;

    public TestDemo(DailyEventRepository EventRepository, DailyLimitRepository LimitRepository, TotalEventRepository totalEventRepository){
        // 테스트 값을 셋팅해주기위해서 repository를 생성자를 통해서 호출한곳에서 repository를 가져옴.
         this.EventRepository=EventRepository;//값대입.
         this.LimitRepository=LimitRepository;//값대입.
         this.TotalRepository=totalEventRepository;//값대입.
    }


    public void testrun()throws Exception{

        testdata();
        LocalDate currentDate = LocalDate.now();//이시간은 ex)17일 0시 0분이다

        for(int i=0; i<15; i++) {

            int peple =(int) (Math.random()*1000) + 50;//하루 참여자는 최소 50명에서 1000명으로 랜덤으로 설정.

            for (int j = 0; j < peple; j++) {
                made_Data(i);
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
        probability_Change(); //위에서 일별평균방문자를 구한값으로 123등의 확률을 조정해줌.
    }

    public void made_Data(int data){

        int number = (int) (Math.random() * daily_Peple) + 1;//랜덤 당첨숫자를 생성해줌. 1~현재날짜까지의 참여자/일수가 최대값이됌.
        LocalDate currentDate=LocalDate.now();//오늘날짜를 생성해줌.
        LocalDate notime=currentDate.plusDays(data);

        if(number<=firstcut) {//랜덤으로 생성된수와 오늘날짜기준으로 변동된 확률의범위의값과 비교함.
            if(first<firstlimit) { //오늘당첨자와 일별당첨자의 수를 비교해서 초과시 2등으로 변경,
                first++;//자바 변수에 오늘 날짜의 fisrt당첨자수를 저장함.
                DailyEvent dailyEvent=DailyEvent.builder().eventDay(eventnumber).luckyDay(notime).ranks("1").build();
                //dailyEvent 객체 생성후 값을 대입해줌.
                EventRepository.save(dailyEvent);//데이터베이스에 값을 삽입해줌.
                return;//모두 실행한뒤 아래로 내려갈수없도록 return시킴.
            }
        }

        if(number<=secondcut) {
            if (second < secondlimit) {//오늘당첨자와 일별당첨자의 수를 비교해서 초과시 3등으로 변경,
                DailyEvent dailyEvent=DailyEvent.builder().eventDay(eventnumber).luckyDay(notime).ranks("2").build();
                EventRepository.save(dailyEvent);
                second++;//자바 변수에 오늘 날짜의 second당첨자수를 저장함.
                return;//모두 실행한뒤 아래로 내려갈수없도록 return시김.
            }
        }

        if(number<=thirdcut){

            if(third < thirdlimit){//오늘당첨자와 일별당첨자의 수를 비교해서 초과시 4등으로 변경,
                DailyEvent dailyEvent=DailyEvent.builder().eventDay(eventnumber).luckyDay(notime).ranks("3").build();
                EventRepository.save(dailyEvent);
                third++;//자바 변수에 오늘 날짜의 third당첨자수를 증감시킴
                return;//모두 실행한뒤 아래로 내려갈수없도록 return시김.
            }
        }

        if(eventnumber==15) {//마지막날의 경우 4등을 제외 1~3등을 우선 당첨후 4등이 당첨돼도록만들어둠.
            if (first != firstlimit || second != secondlimit || third != thirdlimit) {//1~3등이 모두 당첨후 4등이 당첨됌.
                made_Data(data);//확률프로그램을 재실행.
                return;//그후 return시킴.
            }
        }

        //위의 범위값안에 못들었을시 4등당첨후 값 생성후 저장후 종료.
        EventRepository.save(DailyEvent.builder().eventDay(eventnumber).luckyDay(notime).ranks("4").build());
        return;
    }

    public void every(LocalDate currentDate)throws Exception{//기준 17일 으로 주석을 작성함.

        LocalDate newDate = currentDate.minusDays(1);//이날짜는 ex)16일 0시 0분이다.
        String minus_Time=newDate.format(DateTimeFormatter.ofPattern("YYYYMMdd"));;//16일날짜를 String타입으로 변환시킴.

        Optional<TotalEvent> select_Data=TotalRepository.findById(eventnumber);//아이디값을 기준으로 어제날짜의 totalEvent를 가져옴.

        if(select_Data.isPresent()){//optinal을 사용하기위해서 null값체크해줌.
            TotalEvent update_Data = select_Data.get();
            update_Data.setDailyQuota(EventRepository.total_Peple(minus_Time));
            update_Data.setFirstpe(first_pe);//일등의퍼센트 저장 테스트용.
            update_Data.setSecondpe(second_pe);//이등의퍼센트 저장 테스트용.
            update_Data.setThirdpe(third_pe);//삼등으퍼센트 저장 테스트용.
            update_Data.setFirstcut(firstcut);//일등의범위 저장 테스트용.
            update_Data.setSecondcut(secondcut);//이등의범위 저장 테스트용.
            update_Data.setThirdcut(thirdcut);//삼등의범위 저장 테스트용.
            TotalRepository.save(update_Data);//16일 데이터베이스를 업데이트해줌.
        }

        eventnumber++;//이벤트날짜를 증감시켜줌
            String now_Time = currentDate.format(DateTimeFormatter.ofPattern("YYYYMMdd"));//date타입을 String으로 타입변환
            rank_Check(now_Time);//오늘 날짜 ex)17일 기준으로 1~3등의 총당첨자수를 각각변수에 저장.
            limit_Check(now_Time);//오늘 날짜 ex)17일경우 17일기준으로 1~3등의 총당첨돼어야하는 예상값을 각각 변수에 저장해줌.

            TotalEvent new_Data = TotalEvent.builder().luckyDay(currentDate).build(); //17일의 tital2 객체생성.
            TotalRepository.save(new_Data);//객체를 데이터 베이스에 삽입.

            daily_Peple_Update(now_Time); //17일 기준으로 총참여자를 가져온후 이벤트진행일자 2로나누어서 평균일별 참여자를구함.
            probability_Change();//위에서 구한 평균값을 기준으로 123등의 확률을 변동해줌.
    }

    public void limit_Check(String time){
        //이벤트 진행일 기준로하여  총당첨자와 예상총당첨자의 수를 가져와서 차를 구해주고 전날 과락으로 인한 123등의 공백을 채우기위해 limit의 수를 증감시킴.
        // 전날 과락이 없다면 일별 당첨자를 그대로 유지함.
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

    public void probability_Change(){
        //과락당첨자수와 일별참여자수를 계산하여 확률을 조정해줌.
        //double타입으로 변경하여 값을 만들어서 설정해줌.
        double first_Pec= (double) first_pe/(double) 100; //퍼센트값을 현재까지 참여한 평균인원값의 범위로 계산해준다.
        double second_Pec= (double) second_pe/(double) 100;
        double third_Pec= (double) third_pe/(double) 100;

        firstcut = (int) Math.ceil((double)daily_Peple*first_Pec);
        secondcut = (int) Math.ceil((double)daily_Peple*second_Pec);
        thirdcut = (int) Math.ceil((double)daily_Peple*third_Pec);

    }

    public void daily_Peple_Update(String time){
        Optional<Integer> number=TotalRepository.selectTotal_Peple(time);

        int num = number.get() == 0 ? 720 : number.get(); //삼항연산자를 통해서 number 의 값을 체크해줌 0일경우 720를 대입 아니면 number.get()를 대입해줌.

        daily_Peple=(int) num/eventnumber;//참여한인원수 나누기 이벤트날짜로 현날짜까지의 평균 인원수를 구해줌.

        first_pe = (int) Math.ceil((double) firstlimit/(double)daily_Peple*100); //범위를지정하기위해 퍼센테이지를 구해줌. 일별당첨자/총인원*100을해줌
        second_pe = (int) Math.ceil((double) secondlimit/(double)daily_Peple*100);//범위를지정하기위해 퍼센테이지를 구해줌. 일별당첨자/총인원*100을해줌
        third_pe = (int) Math.ceil((double) thirdlimit/(double)daily_Peple*100);//범위를지정하기위해 퍼센테이지를 구해줌. 일별당첨자/총인원*100을해줌
    }

}
