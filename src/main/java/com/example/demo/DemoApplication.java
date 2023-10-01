package com.example.demo;

import com.example.demo.entity.DailyEvent;
import com.example.demo.entity.DailyLimit;
import com.example.demo.entity.TotalEvent;
import com.example.demo.repository.DailyEventRepository;
import com.example.demo.repository.DailyLimitRepository;
import com.example.demo.repository.TotalEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;


@EnableScheduling
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

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



	//repository사용을 위한 autowired 설정
	@Autowired
	private DailyEventRepository EventRepository;

	@Autowired
	private DailyLimitRepository LimitRepository;

	@Autowired
	private TotalEventRepository TotalRepository;


	//DailyLimit에 데이터베이스에 기준값을 설정하기위해 실행줌 또한 서버가 강제종료되거나 문제가 발생했을때 이전의값들을
	// 데이터베이스에서 가져와 자바변수에 재설정해주기위해서 실행됌.
	@Override
	public void run(String... args) throws Exception {
		LocalDate currentDate = LocalDate.now();// 현재시간을 기준으로 date타입생성.
		String time=currentDate.format(DateTimeFormatter.ofPattern("YYYYMMdd"));//Date타입을 String타입과 원하는형식으로 포맷시킴.

		if(TotalRepository.findAll().isEmpty()){//totalRepository에 존재하는 모든값을가져와서 존재여부 체크 있으면 fasle를 반환하여 값을 삽입하지않고 넘어감.
			TotalEvent total= TotalEvent.builder().luckyDay(currentDate).build();//값이없을경우 이벤트 시작날이므로 오늘날짜를 기준으로하여서 데이터를 생성
			TotalRepository.save(total);//위에서 만든 데이터값을 삽입해줌.
		}

		if(LimitRepository.findAll().isEmpty()) {//데이터베이스의 값을 체크하여 있으면 false 없으면 true를 반환하여 값을 체크 후  실행.
			for (int i = 0; i < 5; i++) {//업데이트로 두어야하므로 이건 최초 1회만 실행됄수있도록함.
				DailyLimit daily1 = DailyLimit.builder().first(4).second(17).third(33).build();
				DailyLimit daily2 = DailyLimit.builder().first(3).second(17).third(33).build();
				DailyLimit daily3 = DailyLimit.builder().first(3).second(16).third(34).build();
				LimitRepository.saveAll(Arrays.asList(daily1, daily2, daily3));
			}
		}

		rank_Check(time);//현재시간을 기준으로 오늘 당첨자수를 가져온다.

		limit_Check(time);//현재시간 기준으로 총당첨자-예상총당첨자 계산을하여 일별 당첨자제한을 가져옴.

		daily_Peple_Update(time);//현재시간 기준으로 총응모자를 가져온뒤 이벤트진행일자기준으로 나누어주어 평균일별 참여자를구함.

		change_Odds(); //위에서 일별평균방문자를 구한값으로 123등의 확률을 조정해줌.

		TestDemo TestDemo =new TestDemo(EventRepository,LimitRepository,TotalRepository); //테스트코드 객체 생성. 생성자에 repository넣어줌.
		TestDemo.testrun();// 테스트코드 실행.
	}

	@Scheduled( cron ="01 00 00 17-30 12 *" ) //스케줄러를 사용하여 일단위로 15~30일까지의 확률을 변동시킴과 동시에 데이터베이스에 업데이트함
	public void every()throws Exception{//기준 17일 으로 주석을 작성함.

		LocalDate currentDate = LocalDate.now();// 현재시간을 가져옴. 17일 0시0분01초

		LocalDate newDate = currentDate.minusDays(1);//이날짜는 ex)16일 0시 0분이다.
		String minus_Time=newDate.format(DateTimeFormatter.ofPattern("YYYYMMdd"));;//16일날짜를 String타입으로 변환시킴.

		Optional<TotalEvent> select_Data=TotalRepository.findById(eventnumber);//아이디값을 기준으로 어제날짜의 totalEvent를 가져옴.

		if(select_Data.isPresent()){//Optinal 타입을 TotalEvent타입으로 변환하기위해 null값을 체크해줌.
			TotalEvent update_Data = select_Data.get();//Optinal타입을 객체에 대입시켜줌.
			update_Data.setDailyQuota(EventRepository.total_Peple(minus_Time));//객체에 null값이던dailyQuota의값을셋팅해줌.
			TotalRepository.save(update_Data);//16일 데이터베이스를 업데이트해줌.
		}

		eventnumber++;//이벤트날짜를 증감시켜줌

		String now_Time = currentDate.format(DateTimeFormatter.ofPattern("YYYYMMdd"));//date타입을 String으로 타입변환

		rank_Check(now_Time);//오늘 날짜 ex)17일 기준으로 1~3등의 총당첨자수를 각각변수에 저장.
		limit_Check(now_Time);//오늘 날짜 ex)17일경우 17일기준으로 1~3등의 총당첨돼어야하는 예상값을 각각 변수에 저장해줌.

		TotalEvent new_Data = TotalEvent.builder().luckyDay(currentDate).build(); //17일의 TotalEvent 객체생성.
		TotalRepository.save(new_Data);//객체를 데이터 베이스에 삽입.

		daily_Peple_Update(now_Time); //17일 기준으로 총참여자를 가져온후 이벤트진행일자 2로나누어서 평균일별 참여자를구함.
		change_Odds();//위에서 구한 평균값을 기준으로 123등의 확률을 변동해줌.



	}


	public void made_Data(){

		int number = (int) (Math.random() * daily_Peple) + 1;//랜덤 당첨숫자를 생성해줌. 1~현재날짜까지의 참여자/일수가 최대값이됌.
		LocalDate currentDate=LocalDate.now();//오늘날짜를 생성해줌.

		if(number<=firstcut) {//랜덤으로 생성된수와 오늘날짜기준으로 변동된 확률의범위의값과 비교함.
			if(first<firstlimit) { //오늘당첨자와 일별당첨자의 수를 비교해서 초과시 2등으로 변경,
				first++;//자바 변수에 오늘 날짜의 fisrt당첨자수를 저장함.
				DailyEvent dailyEvent=DailyEvent.builder().eventDay(eventnumber).luckyDay(currentDate).ranks("1").build();
				//dailyEvent 객체 생성후 값을 대입해줌.
				EventRepository.save(dailyEvent);//데이터베이스에 값을 삽입해줌.
				return;//모두 실행한뒤 아래로 내려갈수없도록 return시킴.
			}
		}

		if(number<=secondcut) {
			if (second < secondlimit) {//오늘당첨자와 일별당첨자의 수를 비교해서 초과시 3등으로 변경,
				DailyEvent dailyEvent=DailyEvent.builder().eventDay(eventnumber).luckyDay(currentDate).ranks("2").build();
				EventRepository.save(dailyEvent);
				second++;//자바 변수에 오늘 날짜의 second당첨자수를 저장함.
				return;//모두 실행한뒤 아래로 내려갈수없도록 return시김.
			}
		}

		if(number<=thirdcut){

			if(third < thirdlimit){//오늘당첨자와 일별당첨자의 수를 비교해서 초과시 4등으로 변경,
				System.out.println("3등당첨");
				DailyEvent dailyEvent=DailyEvent.builder().eventDay(eventnumber).luckyDay(currentDate).ranks("3").build();
				EventRepository.save(dailyEvent);
				third++;//자바 변수에 오늘 날짜의 third당첨자수를 증감시킴
				return;//모두 실행한뒤 아래로 내려갈수없도록 return시김.
			}
		}
		//위의 범위값안에 못들었을시 4등당첨후 값 생성후 저장후 종료.
		EventRepository.save(DailyEvent.builder().eventDay(eventnumber).luckyDay(currentDate).ranks("4").build());
		return;
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

	public void change_Odds(){
		//과락당첨자수와 일별참여자수를 계산하여 확률을 조정해줌.
		double first_Pec= (double) first_pe/(double) 100; //계산을 하기위해서 퍼센트로 만들어줌.
		double second_Pec= (double) second_pe/(double) 100;
		double third_Pec= (double) third_pe/(double) 100;

		firstcut = (int) Math.ceil((double)daily_Peple*first_Pec);//범위값을 계산을 통해 셋팅해줌.
		secondcut = (int) Math.ceil((double)daily_Peple*second_Pec);
		thirdcut = (int) Math.ceil((double)daily_Peple*third_Pec);

		int limit=secondlimit+firstlimit+thirdlimit;//limit의 총합값을 구함.
		if(limit>=54 && eventnumber>10){//10일이후에 일별1,2,3등의 당첨자수보다 부족할경우 확률을 1.5배로 올려줌.
			firstcut*=2;
			secondcut*=2;
			thirdcut*=2;
		}
	}

	public void daily_Peple_Update(String time) {
		Optional<Integer> number = TotalRepository.selectTotal_Peple(time);

		int num = number.get() == 0 ? 720 : number.get(); //삼항연산자를 통해서 number 의 값을 체크해줌 0일경우 720를 대입 아니면 number.get()를 대입해줌.

		daily_Peple = (int) num / eventnumber; //일별평균인원을 구해줌.

		first_pe = (int) Math.ceil((double) firstlimit / (double) daily_Peple * 100); //범위를지정하기위해 퍼센테이지를 구해줌. 일별당첨자/총인원*100을해줌
		second_pe = (int) Math.ceil((double) secondlimit / (double) daily_Peple * 100);//범위를지정하기위해 퍼센테이지를 구해줌. 일별당첨자/총인원*100을해줌
		third_pe = (int) Math.ceil((double) thirdlimit / (double) daily_Peple * 100);//범위를지정하기위해 퍼센테이지를 구해줌. 일별당첨자/총인원*100을해줌
	}
}
