package kimjooho.holiday_keeper.holiday.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;
import kimjooho.holiday_keeper.country.entity.Country;
import kimjooho.holiday_keeper.country.repository.CountryRepository;
import kimjooho.holiday_keeper.county.entity.County;
import kimjooho.holiday_keeper.county.repository.CountyRepository;
import kimjooho.holiday_keeper.holiday.dto.HolidayIdResponse;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchResponse;
import kimjooho.holiday_keeper.holiday.dto.HolidaySuper;
import kimjooho.holiday_keeper.holiday.entity.Holiday;
import kimjooho.holiday_keeper.holiday.repository.HolidayRepository;
import kimjooho.holiday_keeper.holiday_county.entity.HolidayCounty;
import kimjooho.holiday_keeper.holiday_county.repository.HolidayCountyRepository;
import kimjooho.holiday_keeper.holiday_type.entity.HolidayType;
import kimjooho.holiday_keeper.holiday_type.repository.HolidayTypeRepository;
import kimjooho.holiday_keeper.nager.NagerHolidayResponse;
import kimjooho.holiday_keeper.type.Type;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayRepository holidayRepository;
    private final HolidayCountyRepository holidayCountyRepository;
    private final HolidayTypeRepository holidayTypeRepository;
    private final CountryRepository countryRepository;
    private final CountyRepository countyRepository;

    @Transactional(readOnly = true)
    public Page<HolidaySearchResponse> search(LocalDate from, LocalDate to, String countryCode, String countyCode,
                                              Type type, Pageable pageable) {

        Page<HolidaySearchResponse> holidaySearchResponses =
                holidayRepository.searchHolidays(from, to, countryCode, countyCode, type, pageable);

        Map<Long, HolidaySearchResponse> idAndHolidaySearchResponse = holidaySearchResponses.getContent().stream()
                .collect(Collectors.toMap(HolidaySearchResponse::getId, Function.identity()));

        Set<Long> holidayIds = idAndHolidaySearchResponse.keySet();

        List<HolidayCounty> holidayCounties = holidayCountyRepository.findAllByHolidayIdIn(holidayIds);
        List<HolidayType> holidayTypes = holidayTypeRepository.findAllByHolidayIdIn(holidayIds);

        holidayCounties
                .forEach(holidayCounty ->
                        addInfo(idAndHolidaySearchResponse, () ->
                                        holidayCounty.getHoliday().getId(),
                                HolidaySearchResponse::addCountyCode,
                                holidayCounty.getCounty().getCode()));

        holidayTypes
                .forEach(holidayType ->
                        addInfo(idAndHolidaySearchResponse, () ->
                                        holidayType.getHoliday().getId(),
                                HolidaySearchResponse::addType,
                                holidayType.getType()));

        return holidaySearchResponses;
    }

    private <T> void addInfo(Map<Long, HolidaySearchResponse> idAndResponse, LongSupplier findId,
                             BiConsumer<HolidaySearchResponse, T> add, T param) {

        HolidaySearchResponse holidaySearchResponse = idAndResponse.get(findId.getAsLong());
        add.accept(holidaySearchResponse, param);
    }

    @Transactional
    public void delete(int year, String countryCode) {
        LocalDate startLocalDate = LocalDate.of(year, 1, 1);
        LocalDate endLocalDate = LocalDate.of(year, 12, 31);

        List<Long> holidayIds =
                holidayRepository.findAllByDateBetweenAndCountryCode(startLocalDate, endLocalDate, countryCode)
                        .stream().map(HolidayIdResponse::getId)
                        .toList();

        holidayCountyRepository.deleteAllByHolidayIdIn(holidayIds);
        holidayTypeRepository.deleteAllByHolidayIdIn(holidayIds);
        holidayRepository.deleteAllByIdIn(holidayIds);
    }

    @Transactional
    public void upsert(int year, String countryCode, List<NagerHolidayResponse> nagerHolidayResponses) {
        List<Holiday> dbHolidays = holidayRepository.selectOneYearHolidays(year, countryCode);

        Map<String, County> countyCodeAndCounty = getAllCounty(nagerHolidayResponses, dbHolidays);
        compareUsingHolidaySuperKey(countryCode, nagerHolidayResponses, dbHolidays, countyCodeAndCounty);
    }

    private Map<String, County> getAllCounty(List<NagerHolidayResponse> nagerHolidayResponses, List<Holiday> holidays) {
        Set<String> nagerCountyCodes = nagerHolidayResponses.stream()
                .flatMap(response -> response.counties().stream())
                .collect(Collectors.toSet());

        List<Long> holidayIds = holidays.stream()
                .map(Holiday::getId)
                .toList();

        Set<County> counties = holidayCountyRepository.findAllByHolidayIdIn(holidayIds).stream()
                .map(HolidayCounty::getCounty)
                .collect(Collectors.toSet());

        Set<String> dbCountyCodes = counties.stream()
                .map(County::getCode)
                .collect(Collectors.toSet());

        nagerCountyCodes.removeAll(dbCountyCodes);

        if (!nagerCountyCodes.isEmpty()) {
            List<County> newCounties = nagerCountyCodes.stream()
                    .map(County::new)
                    .toList();

            counties.addAll(countyRepository.saveAll(newCounties));
        }

        return counties.stream()
                .collect(Collectors.toMap(County::getCode, Function.identity()));
    }

    private void compareUsingHolidaySuperKey(String countryCode, List<NagerHolidayResponse> nagerHolidayResponses,
                                             List<Holiday> dbHolidays, Map<String, County> countyCodeAndCounty) {

        Map<HolidaySuper, NagerHolidayResponse> superAndNagerResponse = nagerHolidayResponses.stream()
                .collect(Collectors.toMap(this::toHolidaySuper, Function.identity()));

        Map<HolidaySuper, Holiday> superAndHoliday = dbHolidays.stream()
                .collect(Collectors.toMap(this::toHolidaySuper, Function.identity()));

        Map<NagerHolidayResponse, Holiday> forUpdateResponseAndHoliday = new HashMap<>();
        List<NagerHolidayResponse> forInsertResponses = new ArrayList<>();

        superAndNagerResponse.forEach((holidaySuper, nagerHolidayResponse) -> {
            if (superAndHoliday.containsKey(holidaySuper)) {
                forUpdateResponseAndHoliday.put(nagerHolidayResponse, superAndHoliday.get(holidaySuper));
                return;
            }

            forInsertResponses.add(nagerHolidayResponse);
        });

        updateHolidayInfo(forUpdateResponseAndHoliday, countyCodeAndCounty);
        saveNewHolidays(countryCode, forInsertResponses, countyCodeAndCounty);
    }

    private HolidaySuper toHolidaySuper(NagerHolidayResponse response) {
        return new HolidaySuper(response.date(), response.name());
    }

    private HolidaySuper toHolidaySuper(Holiday holiday) {
        return new HolidaySuper(holiday.getDate(), holiday.getName());
    }

    private void updateHolidayInfo(Map<NagerHolidayResponse, Holiday> forUpdateResponseAndHoliday,
                                   Map<String, County> countyCodeAndCounty) {

        forUpdateResponseAndHoliday.forEach(((response, holiday) -> holiday.update(response.localName())));

        compareHolidayCounties(forUpdateResponseAndHoliday, countyCodeAndCounty);
        compareHolidayTypes(forUpdateResponseAndHoliday);
    }

    private void compareHolidayCounties(Map<NagerHolidayResponse, Holiday> forUpdateResponseAndHoliday,
                                        Map<String, County> countyCodeAndCounty) {

        List<Long> forUpdateHolidayIds = forUpdateResponseAndHoliday.values().stream()
                .map(Holiday::getId)
                .toList();

        Set<HolidayCounty> dbHolidayCounties = new HashSet<>(
                holidayCountyRepository.findAllByHolidayIdIn(forUpdateHolidayIds));

        Set<HolidayCounty> nagerHolidayCounties = forUpdateResponseAndHoliday.entrySet().stream()
                .flatMap(entry -> {
                    NagerHolidayResponse nagerHolidayResponse = entry.getKey();
                    Holiday holiday = entry.getValue();

                    return nagerHolidayResponse.counties().stream()
                            .map(countyCode -> new HolidayCounty(holiday, countyCodeAndCounty.get(countyCode)));
                })
                .collect(Collectors.toSet());

        Set<HolidayCounty> onlyDbHolidayCounties = new HashSet<>(dbHolidayCounties);
        onlyDbHolidayCounties.removeAll(nagerHolidayCounties);

        holidayCountyRepository.deleteAll(onlyDbHolidayCounties);

        Set<HolidayCounty> onlyNagerHolidayCounties = new HashSet<>(nagerHolidayCounties);
        onlyNagerHolidayCounties.removeAll(dbHolidayCounties);

        holidayCountyRepository.saveAll(onlyNagerHolidayCounties);
    }

    private void compareHolidayTypes(Map<NagerHolidayResponse, Holiday> forUpdateResponseAndHoliday) {
        List<Long> forUpdateHolidayIds = forUpdateResponseAndHoliday.values().stream()
                .map(Holiday::getId)
                .toList();

        Set<HolidayType> dbHolidayTypes = new HashSet<>(
                holidayTypeRepository.findAllByHolidayIdIn(forUpdateHolidayIds));

        Set<HolidayType> nagerHolidayTypes = forUpdateResponseAndHoliday.entrySet().stream()
                .flatMap(entry -> {
                    NagerHolidayResponse nagerHolidayResponse = entry.getKey();
                    Holiday holiday = entry.getValue();

                    return nagerHolidayResponse.types().stream()
                            .map(type -> new HolidayType(holiday, type));
                })
                .collect(Collectors.toSet());

        Set<HolidayType> onlyDbHolidayTypes = new HashSet<>(dbHolidayTypes);
        onlyDbHolidayTypes.removeAll(nagerHolidayTypes);

        holidayTypeRepository.deleteAll(onlyDbHolidayTypes);

        Set<HolidayType> onlyNagerHolidayTypes = new HashSet<>(nagerHolidayTypes);
        onlyNagerHolidayTypes.removeAll(dbHolidayTypes);

        holidayTypeRepository.saveAll(onlyNagerHolidayTypes);
    }

    private void saveNewHolidays(String countryCode, List<NagerHolidayResponse> forInsertResponses,
                                 Map<String, County> countyCodeAndCounty) {

        Country country = countryRepository.getReferenceById(countryCode);

        Map<NagerHolidayResponse, Holiday> responseAndHoliday = forInsertResponses.stream()
                .collect(Collectors.toMap(Function.identity(),
                        response -> new Holiday(country, response.date(), response.name(), response.localName())));

        List<HolidayCounty> newHolidayCounties = new ArrayList<>();
        List<HolidayType> newHolidayTypes = new ArrayList<>();

        responseAndHoliday.forEach((response, holiday) -> {
            List<HolidayCounty> holidayCounties = response.counties().stream()
                    .map(countyCode -> new HolidayCounty(holiday, countyCodeAndCounty.get(countyCode)))
                    .toList();

            newHolidayCounties.addAll(holidayCounties);

            List<HolidayType> holidayTypes = response.types().stream()
                    .map(type -> new HolidayType(holiday, type))
                    .toList();

            newHolidayTypes.addAll(holidayTypes);
        });

        holidayRepository.saveAll(responseAndHoliday.values());
        holidayCountyRepository.saveAll(newHolidayCounties);
        holidayTypeRepository.saveAll(newHolidayTypes);
    }
}
