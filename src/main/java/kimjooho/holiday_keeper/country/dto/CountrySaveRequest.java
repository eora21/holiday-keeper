package kimjooho.holiday_keeper.country.dto;

import kimjooho.holiday_keeper.country.entity.Country;

public record CountrySaveRequest(String code, String name) {

    public Country toEntity() {
        return new Country(code, name);
    }
}
