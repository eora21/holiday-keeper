package kimjooho.holiday_keeper;

import kimjooho.holiday_keeper.holiday.service.HolidayService;
import kimjooho.holiday_keeper.nager.NagerPreparer;
import kimjooho.holiday_keeper.nager.NagerRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class ControllerTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @MockitoBean
    protected NagerPreparer nagerPreparer;

    @MockitoBean
    protected NagerRestClient nagerRestClient;

    @MockitoBean
    protected HolidayService holidayService;
}
