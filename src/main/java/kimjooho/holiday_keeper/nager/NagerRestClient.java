package kimjooho.holiday_keeper.nager;

import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NagerRestClient {
    public static final int MIN_YEAR = 2020;
    public static final int MAX_YEAR = 2025;

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

    public List<NagerCountriesResponse> getCountries() {
        return restClient.get()
                .uri(countries)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public List<NagerHolidaysResponse> getHolidays(int year, String countryCode) {
        return restClient.get()
                .uri(holidays, year, countryCode)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
