package kimjooho.holiday_keeper.holiday.validator;

import jakarta.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StringToLocalDateValidator.class)
public @interface LocalDateValid {

    String message() default "LocalDate로 변환 불가합니다.";

    Class[] groups() default {};

    Class[] payload() default {};

}
