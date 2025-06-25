package kimjooho.holiday_keeper.holiday.dto;

import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HolidaySearchRequestTest {

    @Test
    @DisplayName("year와 from이 같이 입력된 경우, from에 월만 입력됐으면 해당 월의 첫 날짜가 받아져야 한다")
    void checkLastDayWithYearAndFrom() throws Exception {

        // given
        HolidaySearchRequest holidaySearchRequest = new HolidaySearchRequest(25, null, "2", null, null, null);

        // when
        LocalDate from = holidaySearchRequest.getFrom();

        // then
        Assertions.assertThat(from.getDayOfMonth()).isEqualTo(1);
    }

    @Test
    @DisplayName("year와 to가 같이 입력된 경우, to에 월만 입력됐으면 해당 월의 마지막 날짜가 받아져야 한다")
    void checkLastDayWithYearAndTo() throws Exception {

        // given
        HolidaySearchRequest holidaySearchRequest = new HolidaySearchRequest(25, null, null, "2", null, null);

        // when
        LocalDate to = holidaySearchRequest.getTo();

        // then
        Assertions.assertThat(to.getDayOfMonth()).isNotEqualTo(1);
        Assertions.assertThat(to.getDayOfMonth()).isEqualTo(28); // 2025년 2월의 마지막 날은 28일이다.
    }

    @Test
    @DisplayName("year와 from이 입력되지 않은 경우, 시작일은 null이어야 한다")
    void checkFromWithNoDateInfo() throws Exception {

        // given
        HolidaySearchRequest holidaySearchRequest = new HolidaySearchRequest(null, null, null, null, null, null);

        // when
        LocalDate from = holidaySearchRequest.getFrom();

        // then
        Assertions.assertThat(from).isNull();
    }

    @Test
    @DisplayName("year와 from이 입력되지 않은 경우, 종료일은 null이어야 한다")
    void checkToWithNoDateInfo() throws Exception {

        // given
        HolidaySearchRequest holidaySearchRequest = new HolidaySearchRequest(null, null, null, null, null, null);

        // when
        LocalDate to = holidaySearchRequest.getTo();

        // then
        Assertions.assertThat(to).isNull();
    }

    @Test
    @DisplayName("from에 년도가 입력된 경우, year은 무시되어야 한다")
    void ignoreYearWhenFromContainYear() throws Exception {

        // given
        HolidaySearchRequest holidaySearchRequest = new HolidaySearchRequest(2020, null, "20250101", null, null, null);

        // when
        LocalDate from = holidaySearchRequest.getFrom();

        // then
        Assertions.assertThat(from.getYear()).isEqualTo(2025);
    }

    @Test
    @DisplayName("to에 년도가 입력된 경우, year은 무시되어야 한다")
    void ignoreYearWhenToContainYear() throws Exception {

        // given
        HolidaySearchRequest holidaySearchRequest = new HolidaySearchRequest(2020, null, null, "20250101", null, null);

        // when
        LocalDate to = holidaySearchRequest.getTo();

        // then
        Assertions.assertThat(to.getYear()).isEqualTo(2025);
    }

    @Test
    @DisplayName("year가 입력됐으면, from과 to는 year를 기반으로 생성되어야 한다")
    void calculateFromAndToWhenYearNonNull() throws Exception {

        // given
        HolidaySearchRequest holidaySearchRequest = new HolidaySearchRequest(2020, null, null, null, null, null);

        // when
        LocalDate from = holidaySearchRequest.getFrom();
        LocalDate to = holidaySearchRequest.getTo();

        // then
        Assertions.assertThat(from).isEqualTo(LocalDate.of(2020, 1, 1));
        Assertions.assertThat(to).isEqualTo(LocalDate.of(2020, 12, 31));
    }
}
