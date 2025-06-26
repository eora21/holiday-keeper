package kimjooho.holiday_keeper.holiday_type.repository;

import static kimjooho.holiday_keeper.holiday_type.entity.QHolidayType.holidayType;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Collection;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HolidayTypeRepositoryImpl implements HolidayTypeRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void deleteAllByHolidayIdIn(Collection<Long> holidayIds) {
        jpaQueryFactory
                .delete(holidayType)
                .where(holidayType.id.holidayId.in(holidayIds))
                .execute();
    }
}
