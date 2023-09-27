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

	public int eventnumber=1;//이거를바꿔야할꺼같다.

	SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyyMMdd");//데이터베이스와같은형식으로만들어줌

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
		if(dailyLimitRepository.findAll().isEmpty()) {
			for (int i = 0; i < 5; i++) {//업데이트로 두어야하므로 이건 최초 1회만 실행됄수있도록함.
				DailyLimit daily1 = DailyLimit.builder().first(4).second(17).third(33).build();
				DailyLimit daily2 = DailyLimit.builder().first(3).second(17).third(33).build();
				DailyLimit daily3 = DailyLimit.builder().first(3).second(16).third(34).build();
				dailyLimitRepository.saveAll(Arrays.asList(daily1, daily2, daily3));
			}
		}
		Date nowdate=new Date();
		String tlrks=simpleDateFormat.format(nowdate);
		first=dailyEventRepository.firstcheck(tlrks);
		second=dailyEventRepository.secondcheck(tlrks);
		third=dailyEventRepository.thirdcheck(tlrks);
		firstlimit=dailyLimitRepository.totallimitfirstcheck(eventnumber)-dailyEventRepository.totalfirstcheck(tlrks);
		secondlimit=dailyLimitRepository.totallimitsecondcheck(eventnumber)-dailyEventRepository.totalsecondcheck(tlrks);
		thirdlimit=dailyLimitRepository.totallimitthirdcheck(eventnumber)-dailyEventRepository.totalthirdcheck(tlrks);
		secondcut=(int)Math.ceil(secondlimit/7.2);
		thirdcut=(int)Math.ceil(thirdlimit/7.2);
		firstcut=(int)Math.ceil(firstlimit/7.2);
//		System.out.println(first);
//		System.out.println(second);
//		System.out.println(third);
//		System.out.println("--------------------");
//		System.out.println(firstlimit);
//		System.out.println(secondlimit);
//		System.out.println(thirdlimit);
//		System.out.println("--------------------");
//		System.out.println(firstcut);
//		System.out.println(secondcut);
//		System.out.println(thirdcut);
	}

	@Scheduled( cron ="59 59 23 15-30 12 * " ) //스케줄러를 사용하여 일단위로 15~30일까지의 확률을 변동시킴과 동시에 데이터베이스에 업데이트함
	public void every(){
		Date nowdate=new Date();
		String tlrks=simpleDateFormat.format(nowdate);
		TotalEvent total= TotalEvent.builder().luckyday(new Date()).build();
		totalEventRepository.save(total);//오늘 사용할 값을 미리생성해줌 id만존재 나머지는 null값으로 존재
		//luckyday에서 -1 을해줘서 어제날짜 기준으로 값을 수정함 수정할것은 daily_quota<<이것은 daliy_event에서 sum으로 이벤트
		//진행일수 기준으로 가져와서 업데이트해주면됌.
		//그후 각종 변수값들을 초기화 시켜줌.
		String tlrksup="";//여기는 day에 1일더해서 만들어주어야함
		first=dailyEventRepository.firstcheck(tlrksup);
		second=dailyEventRepository.secondcheck(tlrksup);
		third=dailyEventRepository.thirdcheck(tlrksup);
		firstlimit=dailyLimitRepository.totallimitfirstcheck(eventnumber)-dailyEventRepository.totalfirstcheck(tlrksup);
		secondlimit=dailyLimitRepository.totallimitsecondcheck(eventnumber)-dailyEventRepository.totalsecondcheck(tlrksup);
		thirdlimit=dailyLimitRepository.totallimitthirdcheck(eventnumber)-dailyEventRepository.totalthirdcheck(tlrksup);
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
