package kimjooho.holiday_keeper.holiday.controller;

import jakarta.validation.Valid;
import java.util.List;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchRequest;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchResponse;
import kimjooho.holiday_keeper.holiday.service.HolidayService;
import kimjooho.holiday_keeper.nager.NagerHolidayResponse;
import kimjooho.holiday_keeper.nager.NagerRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/holidays")
@RequiredArgsConstructor
public class HolidayController {
    private final HolidayService holidayService;
    private final NagerRestClient restClient;

    @GetMapping
    public ResponseEntity<Page<HolidaySearchResponse>> searchHolidays(
            @Valid @ModelAttribute HolidaySearchRequest searchParamRequest,
            Pageable pageable) {

        Page<HolidaySearchResponse> searchResponse = holidayService.search(searchParamRequest, pageable);
        return ResponseEntity.ok(searchResponse);
    }

    @DeleteMapping("{year}/{countryCode}")
    public ResponseEntity<Void> deleteHolidays(@PathVariable("year") int year,
                                               @PathVariable("countryCode") String countryCode) {

        holidayService.delete(year, countryCode);

        return ResponseEntity.noContent()
                .build();
    }

    @PutMapping("{year}/{countryCode}")
    public ResponseEntity<Void> putHolidays(@PathVariable("year") int year,
                                         @PathVariable("countryCode") String countryCode) {

        List<NagerHolidayResponse> holidaysResponse = restClient.getHolidays(year, countryCode);

        holidayService.upsert(year, countryCode, holidaysResponse);

        return ResponseEntity.ok()
                .build();
    }
}
