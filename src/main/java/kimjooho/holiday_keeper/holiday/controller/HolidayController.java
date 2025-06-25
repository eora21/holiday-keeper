package kimjooho.holiday_keeper.holiday.controller;

import jakarta.validation.Valid;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchRequest;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchResponse;
import kimjooho.holiday_keeper.holiday.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/holidays")
@RequiredArgsConstructor
public class HolidayController {
    private final HolidayService holidayService;

    @GetMapping
    public ResponseEntity<Page<HolidaySearchResponse>> searchHolidays(
            @Valid @ModelAttribute HolidaySearchRequest searchParamRequest,
            Pageable pageable) {

        Page<HolidaySearchResponse> searchResponse = holidayService.search(searchParamRequest, pageable);
        return ResponseEntity.ok(searchResponse);
    }
}
