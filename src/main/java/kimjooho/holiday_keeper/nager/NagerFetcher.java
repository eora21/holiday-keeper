package kimjooho.holiday_keeper.nager;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import kimjooho.holiday_keeper.country.entity.Country;
import kimjooho.holiday_keeper.country.repository.CountryRepository;
import kimjooho.holiday_keeper.county.entity.County;
import kimjooho.holiday_keeper.county.repository.CountyRepository;
import kimjooho.holiday_keeper.holiday.entity.Holiday;
import kimjooho.holiday_keeper.holiday.repository.HolidayRepository;
import kimjooho.holiday_keeper.holiday_county.entity.HolidayCounty;
import kimjooho.holiday_keeper.holiday_county.repository.HolidayCountyRepository;
import kimjooho.holiday_keeper.holiday_type.entity.HolidayType;
import kimjooho.holiday_keeper.holiday_type.repository.HolidayTypeRepository;
import kimjooho.holiday_keeper.util.CpuCoreUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NagerFetcher {
    private static final int MIN_YEAR = 2020;
    private static final int MAX_YEAR = 2025;

    private final NagerRestClient nagerRestClient;
    private final CountryRepository countryRepository;
    private final CountyRepository countyRepository;
    private final HolidayRepository holidayRepository;
    private final HolidayTypeRepository holidayTypeRepository;
    private final HolidayCountyRepository holidayCountyRepository;

    void fetch() {
        List<Country> countries = getCountries();

        try (ExecutorService executorService = Executors.newFixedThreadPool(CpuCoreUtil.CPU_CORE_COUNT * 2)) {
            countries.parallelStream()
                    .forEach(country -> createHolidayInfo(country.getCode(), executorService));

            // 각 국가별 5년치 데이터 획득
            // 평탄화
            // 모든 holiday 추출, saveAll
            // 모든 county 추출, saveAll
            // 모든 holiday-county 추출, saveAll
            // 모든 holiday-type 추출, saveAll
        }
    }

    private List<Country> getCountries() {
        List<Country> countries = nagerRestClient.getCountries().stream()
                .map(response -> new Country(response.countryCode(), response.name()))
                .toList();

        return countryRepository.saveAllAndFlush(countries);
    }

    private void createHolidayInfo(String countryCode, ExecutorService executorService) {
        Map<String, County> countyCodeAndCounty = new ConcurrentHashMap<>();

        List<NagerHolidayResponse> countryHolidaysResponse =
                asyncGetNagerHolidaysResponses(countryCode, executorService);

        countryHolidaysResponse
                .parallelStream()
                .forEach(countryHolidayResponse -> {
                    Country country = countryRepository.getReferenceById(countryCode);

                    Holiday holiday = holidayRepository.save(
                            new Holiday(country, countryHolidayResponse.date(), countryHolidayResponse.name(),
                                    countryHolidayResponse.localName())
                    );

                    List<HolidayType> holidayTypes = countryHolidayResponse.types().stream()
                            .map(type -> new HolidayType(holiday, type))
                            .toList();

                    holidayTypeRepository.saveAll(holidayTypes);

                    List<String> counties = countryHolidayResponse.counties();

                    if (Objects.nonNull(counties) && !counties.isEmpty()) {

                        List<HolidayCounty> holidayCounties = counties.stream()
                                .map(countyCode -> countyCodeAndCounty.computeIfAbsent(countyCode,
                                        code -> countyRepository.save(new County(code))))
                                .map(county -> new HolidayCounty(holiday, county))
                                .toList();

                        holidayCountyRepository.saveAll(holidayCounties);
                    }
                });
    }

    private List<NagerHolidayResponse> asyncGetNagerHolidaysResponses(String countryCode,
                                                                      ExecutorService executorService) {
        List<CompletableFuture<List<NagerHolidayResponse>>> holidayResponsesFutures =
                IntStream.rangeClosed(MIN_YEAR, MAX_YEAR)
                .mapToObj(year -> CompletableFuture.supplyAsync(
                        () -> nagerRestClient.getHolidays(year, countryCode), executorService))
                .toList();

        return holidayResponsesFutures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();
    }
}
