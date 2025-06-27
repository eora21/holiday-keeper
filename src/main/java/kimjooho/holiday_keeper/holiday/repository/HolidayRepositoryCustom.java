package kimjooho.holiday_keeper.holiday.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchResponse;
import kimjooho.holiday_keeper.holiday.entity.Holiday;
import kimjooho.holiday_keeper.type.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HolidayRepositoryCustom {

    Page<HolidaySearchResponse> searchHolidays(LocalDate from, LocalDate to, String countryCode, String countyCode,
                                               Type type, Pageable pageable);

    List<Holiday> selectOneYearHolidays(int year, String countryCode);

    void deleteAllByIdIn(Collection<Long> holidayIds);
}
