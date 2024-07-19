package org.vkartashov.hla4.util;

import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

public class Utils {

    @SneakyThrows
    public static Date parseDate(String date) {
        if (date == null) {
            return null;
        }
        return DateUtils.parseDate(
                date,
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy-MM-dd HH:mm:ss.SSSSSS",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
        );
    }

}
