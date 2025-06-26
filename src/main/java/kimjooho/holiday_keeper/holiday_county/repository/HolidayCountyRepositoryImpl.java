package kimjooho.holiday_keeper.holiday_county.repository;

import static kimjooho.holiday_keeper.holiday_county.entity.QHolidayCounty.holidayCounty;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HolidayCountyRepositoryImpl implements HolidayCountyRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void deleteAllByHolidayIdIn(Collection<Long> holidayIds) {
        jpaQueryFactory
                .delete(holidayCounty)
                .where(holidayCounty.id.holidayId.in(holidayIds))
                .execute();
    }
}
