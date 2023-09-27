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

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
	private DailyEventRepository EventRepository;

	@Autowired
	private DailyLimitRepository LimitRepository;

	@Autowired
	private TotalEventRepository totalRepository;


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	//DailyLimit에 데이터베이스에 기준값을 설정하기위해 실행줌 또한 서버가 강제종료되거나 문제가 발생했을때 이전의값들을
	// 데이터베이스에서 가져와 자바변수에 재설정해주기위해서 실행됌.
	@Override
	public void run(String... args) throws Exception {
		if(totalRepository.findAll().isEmpty()){
			TotalEvent total= TotalEvent.builder().luckyday(new Date()).build();
			totalRepository.save(total);
		}
		if(LimitRepository.findAll().isEmpty()) {
			for (int i = 0; i < 5; i++) {//업데이트로 두어야하므로 이건 최초 1회만 실행됄수있도록함.
				DailyLimit daily1 = DailyLimit.builder().first(4).second(17).third(33).build();
				DailyLimit daily2 = DailyLimit.builder().first(3).second(17).third(33).build();
				DailyLimit daily3 = DailyLimit.builder().first(3).second(16).third(34).build();
				LimitRepository.saveAll(Arrays.asList(daily1, daily2, daily3));
			}
		}
		LocalDate currentDate = LocalDate.now();
		String tlrks=simpleDateFormat.format(currentDate);

		first=EventRepository.firstcheck(tlrks);
		second=EventRepository.secondcheck(tlrks);
		third=EventRepository.thirdcheck(tlrks);

		firstlimit=LimitRepository.totallimitfirstcheck(eventnumber)-EventRepository.totalfirstcheck(tlrks);
		secondlimit=LimitRepository.totallimitsecondcheck(eventnumber)-EventRepository.totalsecondcheck(tlrks);
		thirdlimit=LimitRepository.totallimitthirdcheck(eventnumber)-EventRepository.totalthirdcheck(tlrks);

		secondcut=(int)Math.ceil(secondlimit/7.2);
		thirdcut=(int)Math.ceil(thirdlimit/7.2);
		firstcut=(int)Math.ceil(firstlimit/7.2);
	}

	@Scheduled( cron ="01 00 00 15-30 12 *" ) //스케줄러를 사용하여 일단위로 15~30일까지의 확률을 변동시킴과 동시에 데이터베이스에 업데이트함
	public void every()throws Exception{

		eventnumber++;

		LocalDate currentDate = LocalDate.now();
		String tlrks=simpleDateFormat.format(currentDate);
		TotalEvent total1= TotalEvent
				.builder()
				.luckyday(new Date())
				.dailyQuota(EventRepository.totalpeple(tlrks))
				.build();
		totalRepository.save(total1);//업데이트 시켜줌
		LocalDate newDate = currentDate.plusDays(1);
		String tlrksup=simpleDateFormat.format(newDate);//여기는 day에 1일더해서 만들어주어야함

		first=EventRepository.selctFirst_Check(tlrksup);
		second=EventRepository.selectSecond_Check(tlrksup);
		third=EventRepository.selectThird_Check(tlrksup);

		firstlimit=LimitRepository.selectTotalFirst_Limit(eventnumber)-EventRepository.selectTotalFirst_Check(tlrksup);
		secondlimit=LimitRepository.selectTotaLlimitSecondCheck(eventnumber)-EventRepository.selectTotalSecondCheck(tlrksup);
		thirdlimit=LimitRepository.select_Total_Limit_Third_Check(eventnumber)-EventRepository.selectTotalThirdCheck(tlrksup);

		firstcut=(int)Math.ceil(firstlimit/7.2); // 총이벤트일수 15일동안 총 1~4등당첨을 모두하려면 10800/15=720 이므로
		secondcut=(int)Math.ceil(secondlimit/7.2);// 하루응모인원 기준을 720명으로 잡고 계산하여
		thirdcut=(int)Math.ceil(thirdlimit/7.2); //당첨자수와 예상당첨자수를 비교하여 1~3등의 당첨이 적을경우 확률을 변동시킨다.

		Date tomorrow=simpleDateFormat.parse(tlrksup);
		TotalEvent total2= TotalEvent.builder().luckyday(tomorrow).build();
		totalRepository.save(total2);//내일꺼만들어줌.

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
			if(third <= thirdcut){
				System.out.println("3등당첨");
				third++;
				return "third";
			}
		}

		return "fourth";
	}
}
