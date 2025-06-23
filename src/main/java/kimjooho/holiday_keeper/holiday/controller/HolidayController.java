package kimjooho.holiday_keeper.holiday.controller;

import jakarta.annotation.PostConstruct;
import java.util.List;
import kimjooho.holiday_keeper.country.dto.CountrySaveRequest;
import kimjooho.holiday_keeper.country.entity.Country;
import kimjooho.holiday_keeper.country.service.CountryService;
import kimjooho.holiday_keeper.nager.NagerRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/holidays")
public class HolidayController {
    private final NagerRestClient nagerRestClient;
    private final CountryService countryService;

    @PostConstruct
    private void init() {
        List<CountrySaveRequest> countrySaveRequests = nagerRestClient.getCountries().stream()
                .map(response -> new CountrySaveRequest(response.countryCode(), response.name()))
                .toList();

        List<Country> countries = countryService.saveAll(countrySaveRequests);

        // TODO: Countries 순회, IntStream 2020 ~ 2025, API 호출
        // TODO: County INSERT IGNORE
        // TODO: Holiday INSERT
    }
}
