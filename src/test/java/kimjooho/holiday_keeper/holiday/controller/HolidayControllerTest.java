package kimjooho.holiday_keeper.holiday.controller;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.epages.restdocs.apispec.Schema.schema;
import static com.epages.restdocs.apispec.SimpleType.NUMBER;
import static com.epages.restdocs.apispec.SimpleType.STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.epages.restdocs.apispec.ParameterDescriptorWithType;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import java.time.LocalDate;
import java.util.List;
import kimjooho.holiday_keeper.ControllerTestSupport;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchRequest;
import kimjooho.holiday_keeper.holiday.dto.HolidaySearchResponse;
import kimjooho.holiday_keeper.type.Type;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class HolidayControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("Type은 대소문자 관계 없이 받아올 수 있어야 한다")
    void requestWithType() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/holidays")
                        .param("type", "public")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("잘못된 Type을 입력하면 예외가 발생해야 한다")
    void requestWithWrongType() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/holidays")
                        .param("type", "none")
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("year를 단축하여 작성해도 제대로 동작되어야 한다")
    void requestShortYear() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/holidays")
                        .param("year", "25")
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("2020 이전년도 데이터를 요청하는 경우 예외가 발생해야 한다")
    void requestBefore2020() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/holidays")
                        .param("year", "2019")
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest(name = "[{index}] {0}, {1}")
    @CsvSource(value = {
            "from, 1",
            "from, 11",
            "from, 1111",
            "from, 11-11",
            "from, 11/11",
            "to, 1",
            "to, 11",
            "to, 1111",
            "to, 11-11",
            "to, 11/11"
    })
    @DisplayName("날짜 검색 파라미터(from, to)는 year가 존재하는 경우 다양한 MM/MMdd 형식이 가능해야 한다")
    void requestWithYearAndDateParameters(String paramName, String dateValue) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/holidays")
                        .param("year", "2025")
                        .param(paramName, dateValue)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @ParameterizedTest(name = "[{index}] {0}, {1}")
    @CsvSource(value = {
            "from, 0",
            "from, 13",
            "from, 1132",
            "from, 11-32",
            "from, 11/32",
            "to, 0",
            "to, 13",
            "to, 1132",
            "to, 11-32",
            "to, 11/32"
    })
    @DisplayName("날짜 검색 파라미터(from, to)는 year가 존재해도 잘못된 값은 실패해야 한다")
    void requestWithYearAndWrongDateParameters(String paramName, String dateValue) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/holidays")
                        .param("year", "2025")
                        .param(paramName, dateValue)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest(name = "[{index}] {0}, {1}")
    @CsvSource(value = {
            "from, 1",
            "from, 11",
            "from, 1111",
            "from, 11-11",
            "from, 11/11",
            "to, 1",
            "to, 11",
            "to, 1111",
            "to, 11-11",
            "to, 11/11"
    })
    @DisplayName("날짜 검색 파라미터(from, to)는 year가 존재하지 않는 경우 MM/MMdd 형식이 실패해야 한다")
    void requestWithShortDateParameters(String paramName, String dateValue) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/holidays")
                        .param(paramName, dateValue)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest(name = "[{index}] {0}, {1}")
    @CsvSource(value = {
            "from, 250625",
            "from, 20250625",
            "from, 25-06-25",
            "from, 25/06/25",
            "from, 2025-06-25",
            "from, 2025/06/25",
            "to, 250625",
            "to, 20250625",
            "to, 25-06-25",
            "to, 25/06/25",
            "to, 2025-06-25",
            "to, 2025/06/25",
    })
    @DisplayName("날짜 검색 파라미터(from, to)는 year가 존재하지 않는 경우에도 형식이 맞으면 성공해야 한다")
    void requestWithDateParameters(String paramName, String dateValue) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/holidays")
                        .param(paramName, dateValue)
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("공휴일 검색")
    void searchHolidays() throws Exception {

        HolidaySearchResponse holidaySearchResponse = new HolidaySearchResponse(1L, "KR", "New Year's Day", "새해",
                LocalDate.of(2025, 1, 1));
        holidaySearchResponse.addType(Type.PUBLIC);

        when(holidayService.search(any(LocalDate.class), any(LocalDate.class), anyString(), anyString(),
                any(Type.class), any(Pageable.class)))
                .thenAnswer(invocation -> new PageImpl<>(List.of(holidaySearchResponse), invocation.getArgument(5), 1));


        RequestBuilder request = RestDocumentationRequestBuilders
                .get("/holidays")
                .param("year", "2025")
                .param("countryCode", "us")
                .param("from", "0101")
                .param("to", "1231")
                .param("type", "public")
                .param("countyCode", "us-ks")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                .andDo(document("searchHolidays",
                        resource(ResourceSnippetParameters.builder()
                                .summary("공휴일 검색")
                                .description("여러 파라미터를 통해 공휴일을 검색합니다.")
                                .queryParameters(
                                        new ParameterDescriptorWithType("year").type(NUMBER).optional().description("년도. yyyy, yy 형식을 지원합니다."),
                                        new ParameterDescriptorWithType("countryCode").type(STRING).optional().description("국가 코드"),
                                        new ParameterDescriptorWithType("from").type(STRING).optional().description("검색 시작일. yyyyMMdd 형식으로 변환되며 -, / 구분자를 허용합니다. 년도를 작성한 경우 yyyy를 적지 않아도 지원됩니다. 년도가 작성된 상태에서 월만 적은 경우 해당 년도와 월의 시작 날짜로 변환됩니다."),
                                        new ParameterDescriptorWithType("to").type(STRING).optional().description("검색 종료일. yyyyMMdd 형식으로 변환되며 -, / 구분자를 허용합니다. 년도를 작성한 경우 yyyy를 적지 않아도 지원됩니다. 년도가 작성된 상태에서 월만 적은 경우 해당 년도와 월의 마지막 날짜로 변환됩니다."),
                                        new ParameterDescriptorWithType("type").type(STRING).optional().description("공휴일 타입"),
                                        new ParameterDescriptorWithType("countyCode").type(STRING).optional().description("지역 코드"),
                                        new ParameterDescriptorWithType("page").type(NUMBER).optional().description("페이지"),
                                        new ParameterDescriptorWithType("size").type(NUMBER).optional().description("한 페이지 당 크기"),
                                        new ParameterDescriptorWithType("sort").type(STRING).optional().description("정렬(미구현)")
                                )
                                .responseFields(
                                        fieldWithPath("content[].countryCode").type(STRING).description("국가 코드"),
                                        fieldWithPath("content[].name").type(STRING).description("공휴일명"),
                                        fieldWithPath("content[].localName").type(STRING).description("현지 공휴일명"),
                                        fieldWithPath("content[].date").type(STRING).description("날짜"),
                                        fieldWithPath("content[].countyCodes[]").type(STRING).description("해당 행정 구역"),
                                        fieldWithPath("content[].types[]").type(STRING).description("해당 타입"),
                                        fieldWithPath("page.size").type(STRING).description("요청 페이지 크기"),
                                        fieldWithPath("page.number").type(STRING).description("요청 페이지 순서"),
                                        fieldWithPath("page.totalElements").type(STRING).description("총 결과 수"),
                                        fieldWithPath("page.totalPages").type(STRING).description("총 페이지 수")
                                )
                                .requestSchema(schema(HolidaySearchRequest.class.getName()))
                                .responseSchema(schema(HolidaySearchResponse.class.getName()))
                                .build())));
    }

    @Test
    @DisplayName("공휴일 삭제")
    void deleteHolidays() throws Exception {

        RequestBuilder request = RestDocumentationRequestBuilders
                .delete("/holidays/{year}/{countryCode}", 2025, "KR")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isNoContent())

                .andDo(document("deleteHolidays",
                        resource(ResourceSnippetParameters.builder()
                                .summary("공휴일 삭제")
                                .description("연도, 국가코드와 일치하는 공휴일들을 삭제합니다.")
                                .pathParameters(
                                        new ParameterDescriptorWithType("year").type(NUMBER).description("연도. yyyy 형식을 지원합니다."),
                                        new ParameterDescriptorWithType("countryCode").type(STRING).description("국가 코드")
                                )
                                .build())));
    }

    @Test
    @DisplayName("공휴일 동기화")
    void putHolidays() throws Exception {

        RequestBuilder request = RestDocumentationRequestBuilders
                .put("/holidays/{year}/{countryCode}", 2025, "KR")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())

                .andDo(document("putHolidays",
                        resource(ResourceSnippetParameters.builder()
                                .summary("공휴일 동기화")
                                .description("연도, 국가코드와 일치하는 공휴일들을 불러와 동기화합니다. 이미 존재하던 공휴일은 업데이트되고, 존재하지 않는 공휴일은 추가됩니다.")
                                .pathParameters(
                                        new ParameterDescriptorWithType("year").type(NUMBER).description("연도. yyyy 형식을 지원합니다."),
                                        new ParameterDescriptorWithType("countryCode").type(STRING).description("국가 코드")
                                )
                                .build())));
    }
}
