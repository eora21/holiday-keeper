package kimjooho.holiday_keeper.county.repository;

import kimjooho.holiday_keeper.county.entity.County;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountyRepository extends JpaRepository<County, String> {
}
