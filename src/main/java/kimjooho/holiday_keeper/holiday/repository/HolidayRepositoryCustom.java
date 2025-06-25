package kimjooho.holiday_keeper.holiday.repository;

import kimjooho.holiday_keeper.holiday.dto.HolidaySearchRequest;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayRepositoryCustom {

    Page<HolidaySearchResponse> searchHolidays(HolidaySearchRequest request, Pageable pageable);
}
