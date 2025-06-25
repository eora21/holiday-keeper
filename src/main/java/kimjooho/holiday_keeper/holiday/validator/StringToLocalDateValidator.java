package kimjooho.holiday_keeper.holiday.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import kimjooho.holiday_keeper.util.PlatLocalDateFormatterUtil;

public class StringToLocalDateValidator implements ConstraintValidator<LocalDateValid, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (Objects.nonNull(value)) {
            try {
                PlatLocalDateFormatterUtil.parse(value);
            } catch (DateTimeParseException e) {
                return false;
            }
        }

        return true;
    }
}
