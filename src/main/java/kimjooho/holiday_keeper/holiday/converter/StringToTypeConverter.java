package kimjooho.holiday_keeper.holiday.converter;

import kimjooho.holiday_keeper.type.Type;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToTypeConverter implements Converter<String, Type> {

    @Override
    public Type convert(String source) {
        return Type.valueOf(source.toUpperCase());
    }
}
