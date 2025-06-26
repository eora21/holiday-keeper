package kimjooho.holiday_keeper.holiday_county.repository;

import java.util.Collection;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchRequest;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayCountyRepositoryCustom {

    void deleteAllByHolidayIdIn(Collection<Long> holidayIds);
}
