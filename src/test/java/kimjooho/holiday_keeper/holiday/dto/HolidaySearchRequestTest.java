package kimjooho.holiday_keeper.holiday.dto;

import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HolidaySearchRequestTest {

    @Test
    @DisplayName("year와 to가 같이 입력된 경우, to에 월만 입력됐면 해당 월의 마지막 날짜가 받아져야 한다")
    void checkTo() throws Exception {

        // given
        HolidaySearchRequest holidaySearchRequest = new HolidaySearchRequest(25, null, null, "2", null, null);

        // when
        LocalDate to = holidaySearchRequest.getTo();

        // then
        Assertions.assertThat(to.getDayOfMonth()).isNotEqualTo(1);
        Assertions.assertThat(to.getDayOfMonth()).isEqualTo(28); // 2025년 2월의 마지막 날은 28일이다.
    }
}
