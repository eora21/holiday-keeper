package kimjooho.holiday_keeper.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlatLocalDateFormatterUtil {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static LocalDate parse(String value) {
        return LocalDate.parse(value, formatter);
    }
}
