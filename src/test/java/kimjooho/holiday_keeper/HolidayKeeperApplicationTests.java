package kimjooho.holiday_keeper;

import kimjooho.holiday_keeper.nager.NagerPreparer;
import kimjooho.holiday_keeper.nager.NagerRestClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class HolidayKeeperApplicationTests {

    @MockitoBean
    NagerPreparer nagerPreparer;

    @MockitoBean
    NagerRestClient nagerRestClient;

    @Test
    void contextLoads() {
    }

}
