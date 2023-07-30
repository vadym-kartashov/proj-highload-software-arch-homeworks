package org.vkartashov.hla9.util;

import lombok.SneakyThrows;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

public class DateUtil {

    @SneakyThrows
    public static Date parseDate(String dateString) {
        return DateUtils.parseDate(
                dateString,
                "yyyy-MM-dd",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss.SSS",
                "yyyy-MM-dd HH:mm:ss.SSSSSS",
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
        );
    }

    public static Date generateRandomDateBetween(Date start, Date end) {
        long diff = end.getTime() - start.getTime() + 1;
        return new Date(start.getTime() + (long) (Math.random() * diff));
    }

}
