package kimjooho.holiday_keeper.holiday.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import kimjooho.holiday_keeper.country.entity.Country;
import kimjooho.holiday_keeper.country.repository.CountryRepository;
import kimjooho.holiday_keeper.county.entity.County;
import kimjooho.holiday_keeper.county.repository.CountyRepository;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchResponse;
import kimjooho.holiday_keeper.holiday.entity.Holiday;
import kimjooho.holiday_keeper.holiday.repository.HolidayRepository;
import kimjooho.holiday_keeper.holiday_county.entity.HolidayCounty;
import kimjooho.holiday_keeper.holiday_county.repository.HolidayCountyRepository;
import kimjooho.holiday_keeper.holiday_type.entity.HolidayType;
import kimjooho.holiday_keeper.holiday_type.repository.HolidayTypeRepository;
import kimjooho.holiday_keeper.type.Type;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class HolidayServiceTest {

    @InjectMocks
    HolidayService holidayService;

    @Mock
    HolidayRepository holidayRepository;

    @Mock
    HolidayCountyRepository holidayCountyRepository;

    @Mock
    HolidayTypeRepository holidayTypeRepository;

    @Mock
    CountryRepository countryRepository;

    @Mock
    CountyRepository countyRepository;

    @Test
    @DisplayName("공휴일과 공휴일-행정구역이 잘 조합되어야 한다")
    void combineHolidayCounty() throws Exception {

        // given
        LocalDate localDate = LocalDate.of(2025, 4, 1);
        String aprilFoolsDay = "april fools day";

        when(holidayRepository.searchHolidays(nullable(LocalDate.class), nullable(LocalDate.class),
                nullable(String.class), nullable(String.class), nullable(Type.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(
                        new HolidaySearchResponse(1L, "KR", aprilFoolsDay, "만우절", localDate),
                        new HolidaySearchResponse(2L, "AU", aprilFoolsDay, aprilFoolsDay, localDate)
                )));

        Holiday holiday = new Holiday(new Country("AU", "Australia"), localDate, aprilFoolsDay, aprilFoolsDay);

        Field idField = Holiday.class.getDeclaredField("id");
        idField.setAccessible(true);

        idField.set(holiday, 2L);

        when(holidayCountyRepository.findAllByHolidayIdIn(anySet()))
                .thenReturn(List.of(
                        new HolidayCounty(holiday, new County("AU-ACT")),
                        new HolidayCounty(holiday, new County("AU-NSW"))
                ));

        // when
        Page<HolidaySearchResponse> holidaySearchResponses = holidayService.search(LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 1), null, null, null, Pageable.ofSize(20));

        // then
        Assertions.assertThat(holidaySearchResponses.getTotalElements()).isEqualTo(2);

        Assertions.assertThat(holidaySearchResponses.getContent().getFirst().getCountyCodes())
                .isEmpty();

        Assertions.assertThat(holidaySearchResponses.getContent().getLast().getCountyCodes())
                .contains("AU-ACT", "AU-NSW");
    }

    @Test
    @DisplayName("공휴일과 공휴일-타입이 잘 조합되어야 한다")
    void combineHolidayType() throws Exception {

        // given
        LocalDate localDate = LocalDate.of(2025, 4, 1);
        String aprilFoolsDay = "april fools day";

        when(holidayRepository.searchHolidays(nullable(LocalDate.class), nullable(LocalDate.class),
                nullable(String.class), nullable(String.class), nullable(Type.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(
                        new HolidaySearchResponse(1L, "KR", aprilFoolsDay, "만우절", localDate),
                        new HolidaySearchResponse(2L, "AU", aprilFoolsDay, aprilFoolsDay, localDate)
                )));

        Holiday aprilFoolsDayKr = new Holiday(new Country("KR", "South Korea"), localDate, aprilFoolsDay, "만우절");
        Holiday aprilFoolsDayAu = new Holiday(new Country("AU", "Australia"), localDate, aprilFoolsDay, aprilFoolsDay);

        Field idField = Holiday.class.getDeclaredField("id");
        idField.setAccessible(true);

        idField.set(aprilFoolsDayKr, 1L);
        idField.set(aprilFoolsDayAu, 2L);

        when(holidayTypeRepository.findAllByHolidayIdIn(anySet()))
                .thenReturn(List.of(
                        new HolidayType(aprilFoolsDayKr, Type.PUBLIC),
                        new HolidayType(aprilFoolsDayAu, Type.OPTIONAL),
                        new HolidayType(aprilFoolsDayAu, Type.SCHOOL)
                ));

        // when
        Page<HolidaySearchResponse> holidaySearchResponses = holidayService.search(LocalDate.of(2025, 4, 1),
                LocalDate.of(2025, 4, 1), null, null, null, Pageable.ofSize(20));

        // then
        Assertions.assertThat(holidaySearchResponses.getTotalElements()).isEqualTo(2);

        Assertions.assertThat(holidaySearchResponses.getContent().getFirst().getTypes())
                .contains(Type.PUBLIC);

        Assertions.assertThat(holidaySearchResponses.getContent().getLast().getTypes())
                .contains(Type.OPTIONAL, Type.SCHOOL);
    }
}
