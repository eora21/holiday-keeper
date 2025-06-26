package kimjooho.holiday_keeper.holiday.repository;

import java.util.Collection;
import java.util.List;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchRequest;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchResponse;
import kimjooho.holiday_keeper.holiday.entity.Holiday;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayRepositoryCustom {

    Page<HolidaySearchResponse> searchHolidays(HolidaySearchRequest request, Pageable pageable);

    List<Holiday> selectOneYearHolidays(int year, String countryCode);

    void deleteAllByIdIn(Collection<Long> holidayIds);
}
