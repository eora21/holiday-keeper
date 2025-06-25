package kimjooho.holiday_keeper.holiday.dto;

import jakarta.validation.constraints.Min;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;
import kimjooho.holiday_keeper.holiday.validator.LocalDateValid;
import kimjooho.holiday_keeper.type.Type;
import kimjooho.holiday_keeper.util.PlatLocalDateFormatterUtil;
import lombok.Getter;

public class HolidaySearchRequest {

    @Getter
    @Min(2020)
    private final Integer year;

    @Getter
    private final String countryCode;

    @LocalDateValid
    private final String from;

    @LocalDateValid
    private final String to;

    @Getter
    private final Type type;

    @Getter
    private final String countyCode;

    private final boolean skipDayFlagForTo;

    public HolidaySearchRequest(Integer year, String countryCode, String from, String to, Type type,
                                String countyCode) {

        year = convertYear(year);

        this.year = year;
        this.countryCode = countryCode;
        this.from = convertToLocalDateForm(year, from);
        this.to = convertToLocalDateForm(year, to);
        this.type = type;
        this.countyCode = countyCode;
        this.skipDayFlagForTo = isSkipDay(to);
    }

    private static boolean isSkipDay(String to) {
        return Objects.nonNull(to) && to.length() <= 2;
    }

    private static Integer convertYear(Integer year) {
        if (Objects.isNull(year)) {
            return null;
        }

        if (year < 100) {
            year += 2000;
        }

        return year;
    }

    private static String convertToLocalDateForm(Integer year, String date) {
        if (Objects.isNull(date)) {
            return null;
        }

        String forParse = date.trim()
                .replace("-", "")
                .replace("/", "");

        if (forParse.length() == 6) {
            return "20" + forParse;
        }

        if (Objects.nonNull(year) && forParse.length() == 4) {
            return year + forParse;
        }

        if (Objects.nonNull(year) && forParse.length() == 1) {
            forParse = "0" + forParse;
        }

        if (Objects.nonNull(year) && forParse.length() == 2) {
            return year + forParse + "01";
        }

        return forParse;
    }

    public LocalDate getFrom() {
        if (Objects.nonNull(from)) {
            return PlatLocalDateFormatterUtil.parse(from);
        }

        if (Objects.nonNull(year)) {
            return PlatLocalDateFormatterUtil.parse(year + "0101");
        }

        return null;
    }

    public LocalDate getTo() {
        if (Objects.nonNull(to)) {
            LocalDate parse = PlatLocalDateFormatterUtil.parse(to);

            if (skipDayFlagForTo) {
                return parse.with(TemporalAdjusters.lastDayOfMonth());
            }

            return parse;
        }

        if (Objects.nonNull(year)) {
            return PlatLocalDateFormatterUtil.parse(year + "1231");
        }

        return null;
    }
}
