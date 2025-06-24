package kimjooho.holiday_keeper.county.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "counties")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class County {

    @Id
    @Column(name = "county_code")
    private String code;
}
