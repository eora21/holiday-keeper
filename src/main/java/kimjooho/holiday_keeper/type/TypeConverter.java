package kimjooho.holiday_keeper.type;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TypeConverter implements AttributeConverter<Type, String> {

    @Override
    public String convertToDatabaseColumn(Type type) {
        String typeName = type.name();
        return convertToTitleCase(typeName);
    }

    private String convertToTitleCase(String name) {
        return name.charAt(0) + name.substring(1).toLowerCase();
    }

    @Override
    public Type convertToEntityAttribute(String s) {
        return Type.valueOf(s.toUpperCase());
    }
}
