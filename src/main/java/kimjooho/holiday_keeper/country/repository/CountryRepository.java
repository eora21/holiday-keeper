package kimjooho.holiday_keeper.country.repository;

import kimjooho.holiday_keeper.country.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, String> {
}
