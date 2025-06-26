package kimjooho.holiday_keeper.holiday_type.repository;

import java.util.Collection;
import java.util.List;
import kimjooho.holiday_keeper.holiday_type.entity.HolidayType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayTypeRepository extends JpaRepository<HolidayType, HolidayType.Id>, HolidayTypeRepositoryCustom {

    List<HolidayType> findAllByHolidayIdIn(Collection<Long> holidayIds);
}
