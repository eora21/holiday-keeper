package kimjooho.holiday_keeper.holiday.batch;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class HolidayBatch {

    @Scheduled(cron = "0 0 1 2 1 ?", zone = "Asia/Seoul")
    public void fetchBeforeYearAndNowYear() {
        // 이전년도, 이번년도 동기화
    }
}
