package kimjooho.holiday_keeper.holiday.controller;

import jakarta.validation.Valid;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/holidays")
public class HolidayController {

    @GetMapping
    public ResponseEntity<Void> searchHolidays(@Valid @ModelAttribute HolidaySearchRequest searchParamRequest) {

        // TODO: searchParam 값에 따라 QueryDSL 필터를 동적 추가하여 결과를 반환해야 한다
        return ResponseEntity.ok()
                .build();
    }
}
