package kimjooho.holiday_keeper.holiday_county.repository;

import kimjooho.holiday_keeper.holiday_county.entity.HolidayCounty;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayCountyRepository extends JpaRepository<HolidayCounty, HolidayCounty.Id> {
}
