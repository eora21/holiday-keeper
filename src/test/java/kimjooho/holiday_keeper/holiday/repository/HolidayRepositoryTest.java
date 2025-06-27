package kimjooho.holiday_keeper.holiday.repository;

import static org.assertj.core.groups.Tuple.tuple;

import java.time.LocalDate;
import kimjooho.holiday_keeper.config.QueryDslConfig;
import kimjooho.holiday_keeper.country.entity.Country;
import kimjooho.holiday_keeper.county.entity.County;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchResponse;
import kimjooho.holiday_keeper.holiday.entity.Holiday;
import kimjooho.holiday_keeper.holiday_county.entity.HolidayCounty;
import kimjooho.holiday_keeper.holiday_type.entity.HolidayType;
import kimjooho.holiday_keeper.type.Type;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@DataJpaTest
@Import(QueryDslConfig.class)
class HolidayRepositoryTest {

    @Autowired
    TestEntityManager em;

    @Autowired
    HolidayRepository holidayRepository;

    @Test
    @DisplayName("from 조건이 있는 경우 이후 데이터들이 획득되어야 한다")
    void filterByFrom() throws Exception {

        // given
        Country korea = em.persist(new Country("KR", "South Korea"));
        Country australia = em.persist(new Country("AU", "Australia"));
        em.persist(new Holiday(korea, LocalDate.of(2024, 1, 1), "New Year's Day", "새해"));
        em.persist(new Holiday(korea, LocalDate.of(2024, 12, 25), "Christmas Day", "크리스마스"));
        em.persist(new Holiday(korea, LocalDate.of(2025, 1, 1), "New Year's Day", "새해"));
        em.persist(new Holiday(korea, LocalDate.of(2025, 12, 25), "Christmas Day", "크리스마스"));
        em.persist(new Holiday(australia, LocalDate.of(2024, 1, 1), "New Year's Day", "New Year's Day"));
        em.persist(new Holiday(australia, LocalDate.of(2024, 12, 25), "Christmas Day", "Christmas Day"));
        em.persist(new Holiday(australia, LocalDate.of(2025, 1, 1), "New Year's Day", "New Year's Day"));
        em.persist(new Holiday(australia, LocalDate.of(2025, 12, 25), "Christmas Day", "Christmas Day"));

        // when
        Page<HolidaySearchResponse> holidaySearchResponses = holidayRepository.searchHolidays(LocalDate.of(2025, 12, 1), null, null, null, null, Pageable.ofSize(20));

        // then
        Assertions.assertThat(holidaySearchResponses.getContent())
                .extracting(
                        HolidaySearchResponse::getDate,
                        HolidaySearchResponse::getCountryCode,
                        HolidaySearchResponse::getName,
                        HolidaySearchResponse::getLocalName)
                .contains(
                        tuple(LocalDate.of(2025, 12, 25), "AU", "Christmas Day", "Christmas Day"),
                        tuple(LocalDate.of(2025, 12, 25), "KR", "Christmas Day", "크리스마스")
                );
    }

    @Test
    @DisplayName("to 조건이 있는 경우 이전 데이터들이 획득되어야 한다")
    void filterByTo() throws Exception {

        // given
        Country korea = em.persist(new Country("KR", "South Korea"));
        Country australia = em.persist(new Country("AU", "Australia"));
        em.persist(new Holiday(korea, LocalDate.of(2024, 1, 1), "New Year's Day", "새해"));
        em.persist(new Holiday(korea, LocalDate.of(2024, 12, 25), "Christmas Day", "크리스마스"));
        em.persist(new Holiday(korea, LocalDate.of(2025, 1, 1), "New Year's Day", "새해"));
        em.persist(new Holiday(korea, LocalDate.of(2025, 12, 25), "Christmas Day", "크리스마스"));
        em.persist(new Holiday(australia, LocalDate.of(2024, 1, 1), "New Year's Day", "New Year's Day"));
        em.persist(new Holiday(australia, LocalDate.of(2024, 12, 25), "Christmas Day", "Christmas Day"));
        em.persist(new Holiday(australia, LocalDate.of(2025, 1, 1), "New Year's Day", "New Year's Day"));
        em.persist(new Holiday(australia, LocalDate.of(2025, 12, 25), "Christmas Day", "Christmas Day"));

        // when
        Page<HolidaySearchResponse> holidaySearchResponses = holidayRepository.searchHolidays(null, LocalDate.of(2024, 12, 1), null, null, null, Pageable.ofSize(20));

        // then
        Assertions.assertThat(holidaySearchResponses.getContent())
                .extracting(
                        HolidaySearchResponse::getDate,
                        HolidaySearchResponse::getCountryCode,
                        HolidaySearchResponse::getName,
                        HolidaySearchResponse::getLocalName)
                .contains(
                        tuple(LocalDate.of(2024, 1, 1), "AU", "New Year's Day", "New Year's Day"),
                        tuple(LocalDate.of(2024, 1, 1), "KR", "New Year's Day", "새해")
                );
    }

    @Test
    @DisplayName("from, to 조건이 있는 경우 사이 데이터들이 획득되어야 한다")
    void filterByFromTo() throws Exception {

        // given
        Country korea = em.persist(new Country("KR", "South Korea"));
        Country australia = em.persist(new Country("AU", "Australia"));
        em.persist(new Holiday(korea, LocalDate.of(2024, 1, 1), "New Year's Day", "새해"));
        em.persist(new Holiday(korea, LocalDate.of(2024, 12, 25), "Christmas Day", "크리스마스"));
        em.persist(new Holiday(korea, LocalDate.of(2025, 1, 1), "New Year's Day", "새해"));
        em.persist(new Holiday(korea, LocalDate.of(2025, 12, 25), "Christmas Day", "크리스마스"));
        em.persist(new Holiday(australia, LocalDate.of(2024, 1, 1), "New Year's Day", "New Year's Day"));
        em.persist(new Holiday(australia, LocalDate.of(2024, 12, 25), "Christmas Day", "Christmas Day"));
        em.persist(new Holiday(australia, LocalDate.of(2025, 1, 1), "New Year's Day", "New Year's Day"));
        em.persist(new Holiday(australia, LocalDate.of(2025, 12, 25), "Christmas Day", "Christmas Day"));

        // when
        Page<HolidaySearchResponse> holidaySearchResponses = holidayRepository.searchHolidays(LocalDate.of(2024, 12, 1), LocalDate.of(2025, 1, 1), null, null, null, Pageable.ofSize(20));

        // then
        Assertions.assertThat(holidaySearchResponses.getContent())
                .extracting(
                        HolidaySearchResponse::getDate,
                        HolidaySearchResponse::getCountryCode,
                        HolidaySearchResponse::getName,
                        HolidaySearchResponse::getLocalName)
                .contains(
                        tuple(LocalDate.of(2024, 12, 25), "AU", "Christmas Day", "Christmas Day"),
                        tuple(LocalDate.of(2024, 12, 25), "KR", "Christmas Day", "크리스마스"),
                        tuple(LocalDate.of(2025, 1, 1), "AU", "New Year's Day", "New Year's Day"),
                        tuple(LocalDate.of(2025, 1, 1), "KR", "New Year's Day", "새해")
                );
    }

    @Test
    @DisplayName("국가코드 조건이 있는 경우 해당 국가 데이터만 획득되어야 한다")
    void filterByFromCountryCode() throws Exception {

        // given
        Country korea = em.persist(new Country("KR", "South Korea"));
        Country australia = em.persist(new Country("AU", "Australia"));
        em.persist(new Holiday(korea, LocalDate.of(2024, 1, 1), "New Year's Day", "새해"));
        em.persist(new Holiday(korea, LocalDate.of(2024, 12, 25), "Christmas Day", "크리스마스"));
        em.persist(new Holiday(korea, LocalDate.of(2025, 1, 1), "New Year's Day", "새해"));
        em.persist(new Holiday(korea, LocalDate.of(2025, 12, 25), "Christmas Day", "크리스마스"));
        em.persist(new Holiday(australia, LocalDate.of(2024, 1, 1), "New Year's Day", "New Year's Day"));
        em.persist(new Holiday(australia, LocalDate.of(2024, 12, 25), "Christmas Day", "Christmas Day"));
        em.persist(new Holiday(australia, LocalDate.of(2025, 1, 1), "New Year's Day", "New Year's Day"));
        em.persist(new Holiday(australia, LocalDate.of(2025, 12, 25), "Christmas Day", "Christmas Day"));

        // when
        Page<HolidaySearchResponse> holidaySearchResponses = holidayRepository.searchHolidays(null, null, "KR",null, null, Pageable.ofSize(20));

        // then
        Assertions.assertThat(holidaySearchResponses.getContent())
                .extracting(
                        HolidaySearchResponse::getDate,
                        HolidaySearchResponse::getCountryCode,
                        HolidaySearchResponse::getName,
                        HolidaySearchResponse::getLocalName)
                .contains(
                        tuple(LocalDate.of(2024, 1, 1), "KR", "New Year's Day", "새해"),
                        tuple(LocalDate.of(2024, 12, 25), "KR", "Christmas Day", "크리스마스"),
                        tuple(LocalDate.of(2025, 1, 1), "KR", "New Year's Day", "새해"),
                        tuple(LocalDate.of(2025, 12, 25), "KR", "Christmas Day", "크리스마스")
                );
    }

    @Test
    @DisplayName("행정구역 조건이 있는 경우 해당 행정구역 데이터만 획득되어야 한다")
    void filterByFromCountyCode() throws Exception {

        // given
        Country korea = em.persist(new Country("KR", "South Korea"));
        Country australia = em.persist(new Country("AU", "Australia"));
        County county = em.persist(new County("AU-ACT"));

        Holiday holiday = em.persist(new Holiday(australia, LocalDate.of(2020, 3, 9), "Canberra Day", "Canberra Day"));
        em.persist(new Holiday(korea, LocalDate.of(2024, 1, 1), "New Year's Day", "새해"));
        em.persist(new Holiday(korea, LocalDate.of(2024, 12, 25), "Christmas Day", "크리스마스"));
        em.persist(new Holiday(korea, LocalDate.of(2025, 1, 1), "New Year's Day", "새해"));
        em.persist(new Holiday(korea, LocalDate.of(2025, 12, 25), "Christmas Day", "크리스마스"));
        em.persist(new Holiday(australia, LocalDate.of(2024, 1, 1), "New Year's Day", "New Year's Day"));
        em.persist(new Holiday(australia, LocalDate.of(2024, 12, 25), "Christmas Day", "Christmas Day"));
        em.persist(new Holiday(australia, LocalDate.of(2025, 1, 1), "New Year's Day", "New Year's Day"));
        em.persist(new Holiday(australia, LocalDate.of(2025, 12, 25), "Christmas Day", "Christmas Day"));

        em.persist(new HolidayCounty(holiday, county));

        // when
        Page<HolidaySearchResponse> holidaySearchResponses = holidayRepository.searchHolidays(null, null, null,"AU-ACT", null, Pageable.ofSize(20));

        // then
        Assertions.assertThat(holidaySearchResponses.getContent())
                .extracting(
                        HolidaySearchResponse::getDate,
                        HolidaySearchResponse::getCountryCode,
                        HolidaySearchResponse::getName,
                        HolidaySearchResponse::getLocalName)
                .contains(
                        tuple(LocalDate.of(2020, 3, 9), "AU", "Canberra Day", "Canberra Day")
                );
    }

    @Test
    @DisplayName("타입 조건이 있는 경우 해당 타입 데이터만 획득되어야 한다")
    void filterByFromType() throws Exception {

        // given
        Country korea = em.persist(new Country("KR", "South Korea"));
        Country australia = em.persist(new Country("AU", "Australia"));

        Holiday holiday = em.persist(new Holiday(australia, LocalDate.of(2020, 3, 9), "Canberra Day", "Canberra Day"));
        em.persist(new Holiday(korea, LocalDate.of(2024, 1, 1), "New Year's Day", "새해"));
        em.persist(new Holiday(korea, LocalDate.of(2024, 12, 25), "Christmas Day", "크리스마스"));
        em.persist(new Holiday(korea, LocalDate.of(2025, 1, 1), "New Year's Day", "새해"));
        em.persist(new Holiday(korea, LocalDate.of(2025, 12, 25), "Christmas Day", "크리스마스"));
        em.persist(new Holiday(australia, LocalDate.of(2024, 1, 1), "New Year's Day", "New Year's Day"));
        em.persist(new Holiday(australia, LocalDate.of(2024, 12, 25), "Christmas Day", "Christmas Day"));
        em.persist(new Holiday(australia, LocalDate.of(2025, 1, 1), "New Year's Day", "New Year's Day"));
        em.persist(new Holiday(australia, LocalDate.of(2025, 12, 25), "Christmas Day", "Christmas Day"));

        em.persist(new HolidayType(holiday, Type.PUBLIC));

        // when
        Page<HolidaySearchResponse> holidaySearchResponses = holidayRepository.searchHolidays(null, null, null, null, Type.PUBLIC, Pageable.ofSize(20));

        // then
        Assertions.assertThat(holidaySearchResponses.getContent())
                .extracting(
                        HolidaySearchResponse::getDate,
                        HolidaySearchResponse::getCountryCode,
                        HolidaySearchResponse::getName,
                        HolidaySearchResponse::getLocalName)
                .contains(
                        tuple(LocalDate.of(2020, 3, 9), "AU", "Canberra Day", "Canberra Day")
                );
    }
}
