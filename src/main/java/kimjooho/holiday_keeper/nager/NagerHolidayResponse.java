package kimjooho.holiday_keeper.nager;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import kimjooho.holiday_keeper.type.Type;

public record NagerHolidayResponse(
        LocalDate date,
        String name,
        String localName,
        List<String> counties,
        List<Type> types) {

    @Override
    public List<String> counties() {
        if (Objects.isNull(counties)) {
            return List.of();
        }

        return counties;
    }

    @Override
    public List<Type> types() {
        if (Objects.isNull(types)) {
            return List.of();
        }

        return types;
    }
}
