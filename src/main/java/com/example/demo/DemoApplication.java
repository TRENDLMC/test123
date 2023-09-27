package com.example.demo;

import com.example.demo.entity.DailyLimit;
import com.example.demo.entity.TotalEvent;
import com.example.demo.repository.DailyEventRepository;
import com.example.demo.repository.DailyLimitRepository;
import com.example.demo.repository.TotalEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;


@EnableScheduling
@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

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

	//repository사용을 위한 autowired 설정
	@Autowired
	private DailyEventRepository dailyEventRepository;

	@Autowired
	private DailyLimitRepository dailyLimitRepository;

	@Autowired
	private TotalEventRepository totalEventRepository;


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	//DailyLimit에 데이터베이스에 기준값을 설정하기위해 실행줌 또한 서버가 강제종료되거나 문제가 발생했을때 이전의값들을
	// 데이터베이스에서 가져와 자바변수에 재설정해주기위해서 실행됌.
	@Override
	public void run(String... args) throws Exception {
		for(int i=0; i<5;i++){//업데이트로 두어야하므로 이건 최초 1회만 실행됄수있도록 쿼리와 또사용해야함. 수정해야한다.
			DailyLimit daily1=DailyLimit.builder().first(4).second(17).third(33).build();
			DailyLimit daily2=DailyLimit.builder().first(3).second(17).third(33).build();
			DailyLimit daily3=DailyLimit.builder().first(3).second(16).third(34).build();
			dailyLimitRepository.saveAll(Arrays.asList(daily1,daily2,daily3));
		}
		Date nowDate=new Date();//현재시간을 가져온다
		SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");//데이터베이스와같은형식으로만들어줌
		String tlrks=simpleDateFormat.format(nowDate);//날짜형태변환.
		Optional<Integer> firstch=dailyEventRepository.firstcheck(tlrks);//null값예방을 위하여Optinal사용
		if(firstch.isPresent()){//null이 아닐경우 값을대입 null일경우 위에서 0으로 초기화되었기때문에 0으로 설정됌.
			first=firstch.get();
		}
		Optional<Integer> secondech=dailyEventRepository.secondcheck(tlrks);//위와같은 로직
		if(secondech.isPresent()){
			second=secondech.get();
		}
		Optional<Integer> thirdch=dailyEventRepository.thirdcheck(tlrks);//위와같은 로직.
		if(thirdch.isPresent()){
			third=thirdch.get();
		}
		//firstlimit=dailyevent에서 오늘날짜까지 first의값을 가져오고 - firstlimit의 오늘까지의 fisrt의값의 합을 가져와서 뺴서 넣어주면됌
		//secondlimit=
		//thirdlimit=
		firstcut=(int)Math.ceil(firstlimit/7.2); //첫날이나 시작시 당첨인원은 0명이기떄문에
		secondcut=(int)Math.ceil(secondlimit/7.2);//기본값으로 셋팅이됀다
		thirdcut=(int)Math.ceil(thirdlimit/7.2); //

	}

	@Scheduled( cron ="00 00 24 15-30 12 * " ) //스케줄러를 사용하여 일단위로 15~30일까지의 확률을 변동시킴과 동시에 데이터베이스에 업데이트함
	public void every(){
		TotalEvent total= TotalEvent.builder().luckyday(new Date()).build();
		totalEventRepository.save(total);//오늘 사용할 값을 미리생성해줌 id만존재 나머지는 null값으로 존재
		//luckyday에서 -1 을해줘서 어제날짜 기준으로 값을 수정함 수정할것은 daily_quota<<이것은 daliy_event에서 sum으로 이벤트
		//진행일수 기준으로 가져와서 업데이트해주면됌.
		//그후 각종 변수값들을 초기화 시켜줌.
		//first= 레퍼지토리에서 오늘날짜를 기준으로 sum을 사용하여 first의 합을 가져온다.
		//second=
		//third=
		//firstlimit=레퍼지토리에서 오늘날짜까지 모든 first의 합과 오늘날짜까지의 리미트first값을 - 해서 삽입
		//secondlimit=
		//thirdlimit=
		firstcut=(int)Math.ceil(firstlimit/7.2); // 총이벤트일수 15일동안 총 1~4등당첨을 모두하려면 10800/15=720 이므로
		secondcut=(int)Math.ceil(secondlimit/7.2);// 하루응모인원 기준을 720명으로 잡고 계산하여
		thirdcut=(int)Math.ceil(thirdlimit/7.2); //전날과비교하여 1~3등의 당첨이 적을경우 확률을 변동시킨다.
	}

	public String  random(){
		int number = (int) (Math.random() * 720) + 1;
		if(number>=firstcut) {
			if(first<=firstlimit) {
				System.out.println("1등당첨 체크용");
				first++;
				return "first";
			}
		}
		if(number>=secondcut) {
			if (second <= secondcut) {
				System.out.println("2등당첨");
				second++;
				return "second";
			}
		}
		if(number>=thirdcut){
			if(third<=thirdcut){
				System.out.println("3등당첨");
				third++;
				return "third";
			}
		}
		return "fourth";
	}
}
