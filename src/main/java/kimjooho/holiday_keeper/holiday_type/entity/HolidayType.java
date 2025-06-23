package kimjooho.holiday_keeper.holiday_type.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.io.Serializable;
import kimjooho.holiday_keeper.holiday.entity.Holiday;
import kimjooho.holiday_keeper.type.Type;
import kimjooho.holiday_keeper.type.TypeConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "holiday_types")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HolidayType {

    @EmbeddedId
    private Id id;

    @Getter
    @MapsId("holidayId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holiday_id")
    private Holiday holiday;

    public HolidayType(Holiday holiday, Type type) {
        this.id = new Id(holiday.getId(), type);
        this.holiday = holiday;
    }

    public Type getType() {
        return id.type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HolidayType other) {
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

            @Column(name = "type_code")
            @Convert(converter = TypeConverter.class)
            Type type
    ) implements Serializable {

    }
}
