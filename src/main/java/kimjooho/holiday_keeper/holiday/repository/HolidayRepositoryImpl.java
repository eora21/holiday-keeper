package kimjooho.holiday_keeper.holiday.repository;

import static kimjooho.holiday_keeper.holiday.entity.QHoliday.holiday;
import static kimjooho.holiday_keeper.holiday_county.entity.QHolidayCounty.holidayCounty;
import static kimjooho.holiday_keeper.holiday_type.entity.QHolidayType.holidayType;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchResponse;
import kimjooho.holiday_keeper.holiday.dto.QHolidaySearchResponse;
import kimjooho.holiday_keeper.holiday.entity.Holiday;
import kimjooho.holiday_keeper.type.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static <T> JPAQuery<T> joinOnCountyIfPresent(
            JPAQuery<T> jpaQuery, String countyCode) {

        if (Objects.isNull(countyCode)) {
            return jpaQuery;
        }

        return jpaQuery
                .innerJoin(holidayCounty)
                .on(holidayCounty.id.countyCode.eq(countyCode), holidayCounty.holiday.eq(holiday));
    }

    private static <T> JPAQuery<T> joinOnTypeIfPresent(
            JPAQuery<T> jpaQuery, Type type) {

        if (Objects.isNull(type)) {
            return jpaQuery;
        }

        return jpaQuery
                .innerJoin(holidayType)
                .on(holidayType.id.type.eq(type), holidayType.holiday.eq(holiday));
    }

    private static BooleanExpression filterDateGoeIfPresent(LocalDate date) {
        if (Objects.isNull(date)) {
            return null;
        }

        return holiday.date.goe(date);
    }

    private static BooleanExpression filterDateLoeIfPresent(LocalDate date) {
        if (Objects.isNull(date)) {
            return null;
        }

        return holiday.date.loe(date);
    }

    private static BooleanExpression filterCountryCodeIfPresent(String countryCode) {
        if (Objects.isNull(countryCode)) {
            return null;
        }

        return holiday.country.code.eq(countryCode);
    }

    public Page<HolidaySearchResponse> searchHolidays(LocalDate from, LocalDate to, String countryCode,
                                                      String countyCode, Type type, Pageable pageable) {

        JPAQuery<HolidaySearchResponse> contentSelectFrom = jpaQueryFactory
                .select(new QHolidaySearchResponse(
                        holiday.id,
                        holiday.country.code,
                        holiday.name,
                        holiday.localName,
                        holiday.date
                ))
                .from(holiday);

        JPAQuery<HolidaySearchResponse> contentJoinOnCounty = joinOnCountyIfPresent(contentSelectFrom, countyCode);
        JPAQuery<HolidaySearchResponse> contentJoinOnType = joinOnTypeIfPresent(contentJoinOnCounty, type);

        List<HolidaySearchResponse> content = contentJoinOnType.where(
                        filterDateGoeIfPresent(from),
                        filterDateLoeIfPresent(to),
                        filterCountryCodeIfPresent(countryCode)
                )

                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())

                .orderBy(holiday.date.asc(), holiday.country.code.asc())

                .fetch();

        JPAQuery<Long> countSelectFrom = jpaQueryFactory
                .select(holiday.count())
                .from(holiday);

        JPAQuery<Long> countJoinOnCounty = joinOnCountyIfPresent(countSelectFrom, countyCode);
        JPAQuery<Long> countJoinOnType = joinOnTypeIfPresent(countJoinOnCounty, type);

        JPAQuery<Long> count = countJoinOnType.where(
                filterDateGoeIfPresent(from),
                filterDateLoeIfPresent(to),
                filterCountryCodeIfPresent(countryCode)
        );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }

    public List<Holiday> selectOneYearHolidays(int year, String countryCode) {
        return jpaQueryFactory
                .selectFrom(holiday)

                .where(
                        holiday.date.goe(LocalDate.of(year, 1, 1)),
                        holiday.date.loe(LocalDate.of(year, 12, 31)),
                        holiday.country.code.eq(countryCode)
                )

                .fetch();
    }

    @Override
    public void deleteAllByIdIn(Collection<Long> holidayIds) {
        jpaQueryFactory
                .delete(holiday)
                .where(holiday.id.in(holidayIds))
                .execute();
    }
}
