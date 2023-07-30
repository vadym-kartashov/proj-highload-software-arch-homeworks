package org.vkartashov.hla9.util;

import org.springframework.util.StopWatch;
import org.vkartashov.hla9.MeasurementRoutineItem;

public class StopWatchUtil {

    public static long measureExecutionTime(MeasurementRoutineItem item) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        item.getRunnable().runItem(item.getKey());
        stopWatch.stop();
        return stopWatch.getTotalTimeMillis();
    }

}
