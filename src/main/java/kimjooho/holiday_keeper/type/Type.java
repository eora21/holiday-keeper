package kimjooho.holiday_keeper.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Type {
    PUBLIC,
    BANK,
    SCHOOL,
    AUTHORITIES,
    OPTIONAL,
    OBSERVANCE;

    @JsonCreator
    public static Type toType(String text) {
        return Type.valueOf(text.toUpperCase());
    }
}
