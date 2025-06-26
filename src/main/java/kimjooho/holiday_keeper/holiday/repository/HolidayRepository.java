package kimjooho.holiday_keeper.holiday.repository;

import java.time.LocalDate;
import java.util.List;
import kimjooho.holiday_keeper.holiday.dto.HolidayIdResponse;
import kimjooho.holiday_keeper.holiday.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayRepositoryCustom {

    List<HolidayIdResponse> findAllByDateBetweenAndCountryCode(LocalDate startDate, LocalDate endDate, String countryCode);
}
