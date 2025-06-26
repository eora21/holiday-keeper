package kimjooho.holiday_keeper.holiday_type.repository;

import java.util.Collection;

public interface HolidayTypeRepositoryCustom {

    void deleteAllByHolidayIdIn(Collection<Long> holidayIds);
}
