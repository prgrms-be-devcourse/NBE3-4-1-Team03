package com.app.backend.standard.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ut {

    public class Str {

        public static String localDateTimeToString(final LocalDateTime localDateTime) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime);
        }

    }

}
