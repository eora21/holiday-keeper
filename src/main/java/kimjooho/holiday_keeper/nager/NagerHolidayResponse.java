package kimjooho.holiday_keeper.nager;

import java.time.LocalDate;
import java.util.List;
import kimjooho.holiday_keeper.type.Type;

record NagerHolidayResponse(
        LocalDate date,
        String name,
        String localName,
        List<String> counties,
        List<Type> types
        ) {
}
