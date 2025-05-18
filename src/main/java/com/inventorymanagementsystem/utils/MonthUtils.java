package com.inventorymanagementsystem.utils;

import java.time.Month;
import java.util.AbstractMap;
import java.util.Map;

public class MonthUtils {

    public static final Map<Month, String> MONTH_TRANSLATIONS = Map.ofEntries(
            entry(Month.JANUARY, "OCAK"),
            entry(Month.FEBRUARY, "ŞUBAT"),
            entry(Month.MARCH, "MART"),
            entry(Month.APRIL, "NİSAN"),
            entry(Month.MAY, "MAYIS"),
            entry(Month.JUNE, "HAZİRAN"),
            entry(Month.JULY, "TEMMUZ"),
            entry(Month.AUGUST, "AĞUSTOS"),
            entry(Month.SEPTEMBER, "EYLÜL"),
            entry(Month.OCTOBER, "EKİM"),
            entry(Month.NOVEMBER, "KASIM"),
            entry(Month.DECEMBER, "ARALIK")
    );

    public static String translate(Month month) {
        return MONTH_TRANSLATIONS.getOrDefault(month, month.name());
    }

    private static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }
}
