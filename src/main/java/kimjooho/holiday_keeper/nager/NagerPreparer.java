package kimjooho.holiday_keeper.nager;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NagerPreparer {
    private final NagerFetcher nagerFetcher;

    @PostConstruct
    private void prepare() {
        nagerFetcher.fetch();
    }
}
