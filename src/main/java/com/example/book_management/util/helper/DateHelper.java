package com.example.book_management.util.helper;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DateHelper {
    private static final String yyyy_MM_dd = "yyyy-MM-dd";
    public static final DateTimeFormatter yyyyMMddFormatter = DateTimeFormatter.ofPattern(yyyy_MM_dd);

    public static LocalDate parseBEToCE(String beDate) {
        String[] parts = beDate.split("-");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd");
        }
    
        int beYear = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        int ceYear = beYear - 543;
    
        try {
            return LocalDate.of(ceYear, month, day);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Invalid date (day/month) for given year");
        }
    }  

    public static String formatCEToBE(LocalDate ceDate) {
        if (ceDate == null) return null;
    
        int beYear = ceDate.getYear() + 543;
        LocalDate beDate = LocalDate.of(beYear, ceDate.getMonth(), ceDate.getDayOfMonth());
        return beDate.format(yyyyMMddFormatter);
    }

    public static void validateYear(LocalDate ceDate, int minYear, int maxYear) {
        int year = ceDate.getYear();
        if (year < minYear || year > maxYear) {
            throw new IllegalArgumentException("Year must be between " + minYear + " and " + maxYear);
        }
    }
}
