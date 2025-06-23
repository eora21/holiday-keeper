package kimjooho.holiday_keeper.nager;

import java.time.LocalDate;
import java.util.List;
import kimjooho.holiday_keeper.type.Type;

public record NagerHolidaysResponse(
        LocalDate date,
        String name,
        String localName,
        String countryCode,
        List<String> counties,
        List<Type> types
        ) {
}
