package br.com.vcruz.stock.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateConverterUtils {

    public static LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }

        return date.toInstant().atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static Date convertToDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }

        return Date.from(date.atZone(ZoneId.systemDefault())
                .toInstant());
    }
}
