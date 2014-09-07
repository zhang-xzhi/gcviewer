package allen.gc.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * DateUtil.
 * 
 * @author xinzhi.zhang
 * */
public class DateUtil {

    public static final String SecondFormat = "yyyy-MM-dd HH:mm:ss";
    public static final String MinuteFormat = "yyyy-MM-dd HH:mm";
    public static final String HourFormat   = "yyyy-MM-dd HH";
    public static final String DayFormat    = "yyyy-MM-dd";

    public static String formatDate(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(true);
        return dateFormat.format(date);
    }

    public static Date parseDate(String date) {
        if (date == null || date.equals("")) {
            return null;
        }

        Date result = null;

        result = parseDate(date, SecondFormat);
        if (result != null) {
            return result;
        }

        result = parseDate(date, MinuteFormat);
        if (result != null) {
            return result;
        }

        result = parseDate(date, HourFormat);
        if (result != null) {
            return result;
        }
        result = parseDate(date, DayFormat);
        if (result != null) {
            return result;
        }

        throw new RuntimeException("date=" + date);

    }

    public static Date parseDate(String date, String format) {
        if (date == null || date.equals("")) {
            return null;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setLenient(true);
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
}
