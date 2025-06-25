package kimjooho.holiday_keeper.holiday.repository;

import kimjooho.holiday_keeper.holiday.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HolidayRepository extends JpaRepository<Holiday, Long>, HolidayRepositoryCustom {
}
