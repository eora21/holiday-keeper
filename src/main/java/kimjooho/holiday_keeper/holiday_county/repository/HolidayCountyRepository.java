package kimjooho.holiday_keeper.holiday_county.repository;

import java.util.Collection;
import java.util.List;
import kimjooho.holiday_keeper.holiday_county.entity.HolidayCounty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayCountyRepository extends JpaRepository<HolidayCounty, HolidayCounty.Id>, HolidayCountyRepositoryCustom {

    List<HolidayCounty> findAllByHolidayIdIn(Collection<Long> holidayIds);
}
