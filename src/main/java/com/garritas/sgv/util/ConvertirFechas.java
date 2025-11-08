package com.garritas.sgv.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertirFechas {

    // Formato de fecha
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }
}
