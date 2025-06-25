package kimjooho.holiday_keeper.holiday_county.repository;

import java.util.List;
import java.util.Set;
import kimjooho.holiday_keeper.holiday_county.entity.HolidayCounty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayCountyRepository extends JpaRepository<HolidayCounty, HolidayCounty.Id> {

    List<HolidayCounty> findAllByHolidayIdIn(Set<Long> holidayIds);
}
