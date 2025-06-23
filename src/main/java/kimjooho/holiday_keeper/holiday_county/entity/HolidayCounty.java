package kimjooho.holiday_keeper.holiday_county.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serializable;
import kimjooho.holiday_keeper.county.entity.County;
import kimjooho.holiday_keeper.holiday.entity.Holiday;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "holiday_counties")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HolidayCounty {

    @EmbeddedId
    private Id id;

    @Getter
    @MapsId("holidayId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id")
    private Holiday holiday;

    @Getter
    @MapsId("countyCode")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "county_code")
    private County county;

    public HolidayCounty(Holiday holiday, County county) {
        this.id = new Id(holiday.getId(), county.getCode());
        this.holiday = holiday;
        this.county = county;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HolidayCounty other) {
            return id.equals(other.id);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Embeddable
    public record Id(
            @Column
            Long holidayId,

            @Column
            String countyCode
    ) implements Serializable {

    }
}
