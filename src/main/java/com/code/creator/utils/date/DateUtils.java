package com.code.creator.utils.date;

import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;

/**
 * This is the class comment for the class {@link DateUtils}.
 *
 * @author zhiyuan
 * @since 2021-6-15 14:44:51
 */
public class DateUtils {

    private static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
    private static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private DateUtils() {
    }

    public static <T> T parse(@Nonnull String text, Class<T> cls) {
        return parse(text, "", cls);
    }

    public static <T> T parse(@Nonnull String text, String pattern, Class<T> cls) {
        if (cls == Instant.class) {
            return cls.cast(Instant.ofEpochSecond(Long.parseLong(text)));
        } else if (cls == LocalDate.class) {
            return cls.cast(LocalDate.parse(text, DateTimeFormatter.ofPattern(StringUtils.hasText(pattern) ? pattern : DEFAULT_DATE_PATTERN)));
        } else if (cls == LocalDateTime.class) {
            if (NumberUtils.isDigits(text)) {
                /*Timestamp => LocalDateTime*/
                return cls.cast(LocalDateTime.ofInstant(parse(text, null, Instant.class), ZoneId.systemDefault()));
            }
            return cls.cast(LocalDateTime.parse(text, DateTimeFormatter.ofPattern(StringUtils.hasText(pattern) ? pattern : DEFAULT_DATE_TIME_PATTERN)));
        }
        throw new RuntimeException("The " + cls + " is unsupported.");
    }

    public static String parse(Temporal temporal) {
        return parse(temporal, null);
    }

    public static String parse(@Nonnull Temporal temporal, String pattern) {
        if (temporal instanceof Instant instant) {
            return parse(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
        } else if (temporal instanceof LocalDate localDate) {
            return DateTimeFormatter.ofPattern(StringUtils.hasText(pattern) ? pattern : DEFAULT_DATE_PATTERN).format(localDate);
        } else if (temporal instanceof LocalDateTime localDateTime) {
            return DateTimeFormatter.ofPattern(StringUtils.hasText(pattern) ? pattern : DEFAULT_DATE_TIME_PATTERN).format(localDateTime);
        }
        throw new RuntimeException("The " + temporal.getClass() + " is unsupported.");
    }

    /*获取当前时间*/
    public static LocalDateTime getNow() {
        return LocalDateTime.now().withNano(0);
    }
}