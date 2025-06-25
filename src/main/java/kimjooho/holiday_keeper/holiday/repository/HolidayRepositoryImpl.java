package kimjooho.holiday_keeper.holiday.repository;

import static kimjooho.holiday_keeper.holiday.entity.QHoliday.holiday;
import static kimjooho.holiday_keeper.holiday_county.entity.QHolidayCounty.holidayCounty;
import static kimjooho.holiday_keeper.holiday_type.entity.QHolidayType.holidayType;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchRequest;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchResponse;
import kimjooho.holiday_keeper.holiday.dto.QHolidaySearchResponse;
import kimjooho.holiday_keeper.type.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@RequiredArgsConstructor
public class HolidayRepositoryImpl implements HolidayRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private static <T> JPAQuery<T> joinOnCountyIfPresentCounty(
            JPAQuery<T> getFromHoliday, String countyCode) {

        if (Objects.isNull(countyCode)) {
            return getFromHoliday;
        }

        return getFromHoliday
                .innerJoin(holidayCounty)
                .on(holidayCounty.id.countyCode.eq(countyCode), holidayCounty.holiday.eq(holiday));
    }

    private static <T> JPAQuery<T> joinOnTypeIfPresentCounty(
            JPAQuery<T> joinOnCounty, Type type) {

        if (Objects.isNull(type)) {
            return joinOnCounty;
        }

        return joinOnCounty
                .innerJoin(holidayType)
                .on(holidayType.id.type.eq(type), holidayType.holiday.eq(holiday));
    }

    private static BooleanExpression dateGoeIfPresentFrom(LocalDate from) {
        if (Objects.isNull(from)) {
            return null;
        }

        return holiday.date.goe(from);
    }

    private static BooleanExpression dateLoeIfPresentTo(LocalDate to) {
        if (Objects.isNull(to)) {
            return null;
        }

        return holiday.date.loe(to);
    }

    public Page<HolidaySearchResponse> searchHolidays(HolidaySearchRequest request, Pageable pageable) {
        JPAQuery<HolidaySearchResponse> contentSelectFrom = jpaQueryFactory
                .select(new QHolidaySearchResponse(
                        holiday.id,
                        holiday.country.code,
                        holiday.name,
                        holiday.localName,
                        holiday.date
                ))
                .from(holiday);

        JPAQuery<HolidaySearchResponse> contentJoinOnCounty = joinOnCountyIfPresentCounty(contentSelectFrom, request.getCountyCode());
        JPAQuery<HolidaySearchResponse> contentJoinOnType = joinOnTypeIfPresentCounty(contentJoinOnCounty, request.getType());

        List<HolidaySearchResponse> content = contentJoinOnType.where(
                        dateGoeIfPresentFrom(request.getFrom()),
                        dateLoeIfPresentTo(request.getTo())
                )

                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())

                .orderBy(holiday.date.asc(), holiday.country.code.asc())

                .fetch();

        JPAQuery<Long> countSelectFrom = jpaQueryFactory
                .select(holiday.count())
                .from(holiday);

        JPAQuery<Long> countJoinOnCounty = joinOnCountyIfPresentCounty(countSelectFrom, request.getCountyCode());
        JPAQuery<Long> countJoinOnType = joinOnTypeIfPresentCounty(countJoinOnCounty, request.getType());

        JPAQuery<Long> count = countJoinOnType.where(
                        dateGoeIfPresentFrom(request.getFrom()),
                        dateLoeIfPresentTo(request.getTo())
                );

        return PageableExecutionUtils.getPage(content, pageable, count::fetchOne);
    }
}
