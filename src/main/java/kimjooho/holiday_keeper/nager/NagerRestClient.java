package kimjooho.holiday_keeper.nager;

import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class NagerRestClient {
    private RestClient restClient;

    @Value("${date.nager.at.base-url}")
    private String baseUrl;

    @Value("${date.nager.at.countries}")
    private String countries;

    @Value("${date.nager.at.holidays}")
    private String holidays;

    @PostConstruct
    private void init() {
        this.restClient = RestClient.create(baseUrl);
    }

    List<NagerCountriesResponse> getCountries() {
        return restClient.get()
                .uri(countries)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    List<NagerHolidayResponse> getHolidays(int year, String countryCode) {
        return restClient.get()
                .uri(holidays, year, countryCode)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
