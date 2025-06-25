package kimjooho.holiday_keeper.advice;

import java.util.Objects;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BindExceptionAdvice {
    private static String getDefaultMessage(FieldError fieldError) {
        if (fieldError.isBindingFailure()) {
            return "유효한 값이 아닙니다.";
        }

        String defaultMessage = fieldError.getDefaultMessage();

        if (Objects.isNull(defaultMessage)) {
            return "조건이 충족되지 않았습니다.";
        }

        return defaultMessage;
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException exception) {
        MultiValueMap<String, String> errorMessages = new LinkedMultiValueMap<>();

        exception.getFieldErrors()
                .forEach(fieldError -> errorMessages.add(fieldError.getField(), getDefaultMessage(fieldError)));

        return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorMessages);
    }
}
