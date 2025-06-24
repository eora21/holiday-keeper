package kimjooho.holiday_keeper.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CpuCoreUtil {
    public static final int CPU_CORE_COUNT = Runtime.getRuntime().availableProcessors();
}
