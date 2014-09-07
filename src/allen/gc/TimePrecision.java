package allen.gc;

import allen.gc.util.DateUtil;

/**
 * TimePrecision.
 * 
 * @author xinzhi.zhang
 * */
public enum TimePrecision {

    Day(1000L * 60 * 60 * 24, DateUtil.DayFormat),

    Hour(1000L * 60 * 60, DateUtil.HourFormat),

    Minutes30(1000L * 60 * 30, DateUtil.MinuteFormat),

    Minutes10(1000L * 60 * 10, DateUtil.MinuteFormat),

    Minutes5(1000L * 60 * 5, DateUtil.MinuteFormat),

    Minute(1000L * 60, DateUtil.MinuteFormat),

    Seconds30(1000L * 30, DateUtil.SecondFormat),

    Seconds10(1000L * 10, DateUtil.SecondFormat),

    Seconds5(1000L * 5, DateUtil.SecondFormat),

    Second(1000L, DateUtil.SecondFormat);

    private long   milliSeconds;
    private String format;

    private TimePrecision(long milliSeconds, String format) {
        this.milliSeconds = milliSeconds;
        this.format = format;
    }

    public long getMilliSeconds() {
        return milliSeconds;
    }

    public String getFormat() {
        return format;
    }
}