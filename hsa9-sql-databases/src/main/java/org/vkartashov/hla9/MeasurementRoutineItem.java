package org.vkartashov.hla9;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MeasurementRoutineItem {

    private String key;
    private RoutineItemRunnable runnable;

}
