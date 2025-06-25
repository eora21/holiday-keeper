package kimjooho.holiday_keeper.holiday.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.querydsl.core.annotations.QueryProjection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import kimjooho.holiday_keeper.type.Type;
import lombok.Getter;

@Getter
public class HolidaySearchResponse {

    @JsonIgnore
    private final Long id;

    private final String countryCode;

    private final String name;

    private final String localName;

    private final LocalDate date;

    private final List<String> countyCodes = new ArrayList<>();

    private final List<Type> types = new ArrayList<>();

    @QueryProjection
    public HolidaySearchResponse(Long id, String countryCode, String name, String localName, LocalDate date) {
        this.id = id;
        this.countryCode = countryCode;
        this.name = name;
        this.localName = localName;
        this.date = date;
    }

    public void addCountyCode(String countyCode) {
        this.countyCodes.add(countyCode);
    }

    public void addType(Type type) {
        this.types.add(type);
    }
}
