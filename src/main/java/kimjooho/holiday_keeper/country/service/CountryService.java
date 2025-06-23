package kimjooho.holiday_keeper.country.service;

import java.util.List;
import kimjooho.holiday_keeper.country.dto.CountrySaveRequest;
import kimjooho.holiday_keeper.country.entity.Country;
import kimjooho.holiday_keeper.country.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    public List<Country> saveAll(List<CountrySaveRequest> countrySaveRequests) {
        List<Country> countries = countrySaveRequests.stream()
                .map(CountrySaveRequest::toEntity)
                .toList();

        return countryRepository.saveAll(countries);
    }
}
