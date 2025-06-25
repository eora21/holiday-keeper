package kimjooho.holiday_keeper.holiday.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchRequest;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchResponse;
import kimjooho.holiday_keeper.holiday.repository.HolidayRepository;
import kimjooho.holiday_keeper.holiday_county.entity.HolidayCounty;
import kimjooho.holiday_keeper.holiday_county.repository.HolidayCountyRepository;
import kimjooho.holiday_keeper.holiday_type.entity.HolidayType;
import kimjooho.holiday_keeper.holiday_type.repository.HolidayTypeRepository;
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

    @Transactional(readOnly = true)
    public Page<HolidaySearchResponse> search(HolidaySearchRequest request, Pageable pageable) {
        Page<HolidaySearchResponse> holidaySearchResponses = holidayRepository.searchHolidays(request, pageable);

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
}
