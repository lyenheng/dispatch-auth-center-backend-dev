package com.kedacom.dispatch.ac.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.kedacom.dispatch.ac.common.constant.DateConstant.*;


/**
 * @author n
 */
public class DateUtils {
    private DateUtils() {
    }

    /**
     * Calendar -> String
     */
    public static String format(Calendar cal) {
        return format(cal.getTime());
    }


    /**
     * Calendar,String -> String
     */
    public static String format(Calendar cal, String pattern) {
        return format(cal.getTime(), pattern);
    }

    /**
     * Calendar,DateFormat -> String
     */
    public static String format(Calendar cal, DateFormat df) {
        return format(cal.getTime(), df);
    }

    /**
     * Date -> String
     */
    public static String format(Date date) {
        return format(date, DF_CN);
    }

    /**
     * Date,String -> String
     */
    public static String format(Date date, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return format(date, df);
    }

    public static String format(long ts, DateFormat df) {
        return format(new Date(ts), df);
    }

    /**
     * Date,DateFormat -> String
     */
    public static String format(Date date, DateFormat df) {
        if (date == null) {
            return "";
        }

        if (df != null) {
            return df.format(date);
        }
        return DF_CN.format(date);
    }

    /**
     * String -> Calendar
     */
    public static Calendar parse(String strDate) {
        return parse(strDate, null);
    }

    /**
     * String,DateFormate -> Calendar
     */
    public static Calendar parse(String strDate, DateFormat df) {
        Date date = parseDate(strDate, df);
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * String -> Date
     */
    public static Date parseDate(String strDate) {
        return parseDate(strDate, null);
    }

    /**
     * String,DateFormate -> Date
     */
    public static Date parseDate(String strDate, DateFormat df) {
        if (df == null) {
            df = DF_CN;
        }
        ParsePosition parseposition = new ParsePosition(0);

        return df.parse(strDate, parseposition);
    }

    public static long getGapMin(long minsec) {
        return minsec / 60000;
    }

    public static long getGapMinByAddtime(long addtime) {
        return getGapMinByAddtime(addtime, System.currentTimeMillis());
    }

    public static long getGapMinByAddtime(long addtime, long current) {
        return getGapMin(current - addtime);
    }

    public static String getGapMinStirngByAddtime(long addtime, long current) {
        return getMinStirngBySubTime(getGapMin(current - addtime));
    }

    public static String getMinStirngBySubTime(long min) {
        long hour = min / 60;
        long restmin = min % 60;
        return (hour > 0 ? hour + "时" : "") + restmin + "分";
    }


    public static Calendar parseDateString(String str, String format) throws ParseException {
        if (str == null) {
            return null;
        }
        Date date = null;
        SimpleDateFormat df = new SimpleDateFormat(format);

        date = df.parse(str);

        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * returns the current date in the default format
     */
    public static String getToday() {
        return format(new Date());
    }

    public static Date getYesterday() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);

        return cal.getTime();
    }

    public static Calendar getFirstDayOfMonth() {
        Calendar cal = getNow();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);


        return cal;
    }

    public static Calendar getNow() {
        return Calendar.getInstance();
    }

    /**
     * add some month from the date
     */
    public static Date addMonth(Date date, int n) throws Exception {
        Calendar cal = getNow();
        cal.setTime(date);
        cal.add(Calendar.MONTH, n);
        return cal.getTime();
    }

    public static int daysBetween(Date returnDate) {
        return daysBetween(null, returnDate);
    }

    public static long tirmDay(Calendar time) {//得到当天的0点时间
        time.set(Calendar.HOUR_OF_DAY, 0);
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MILLISECOND, 0);
        return time.getTimeInMillis();
    }

    public static int daysBetween(Date now, Date returnDate) {
        if (returnDate == null) {
            return 0;
        }

        Calendar cNow = getNow();
        Calendar cReturnDate = getNow();
        if (now != null) {
            cNow.setTime(now);
        }
        cReturnDate.setTime(returnDate);
        setTimeToMidnight(cNow);
        setTimeToMidnight(cReturnDate);
        long nowMs = cNow.getTimeInMillis();
        long returnMs = cReturnDate.getTimeInMillis();
        return millisecondsToDays(nowMs - returnMs);
    }

    private static int millisecondsToDays(long intervalMs) {
        return (int) (intervalMs / (1000 * 86400));
    }

    private static void setTimeToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
    }

    public static String formatDate(Object obj, String format) {
        String result = "";

        Date date = (Date) obj;
        result = format(date, format);

        return result;
    }

    public static String formatDate(Object obj) {
        return formatDate(obj, SHORT_DATE);
    }

    public static String getSunday(String date) throws ParseException {
        Calendar c = DateUtils.parseDateString(date, "yyyy-MM-dd");
        int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
        c.add(Calendar.DATE, 1 - dayofweek);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(c.getTime());
    }

    public static String getLastDayOfWeek(String date) throws ParseException {
        Calendar c = DateUtils.parseDateString(date, "yyyy-MM-dd");
        int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;

        c.add(Calendar.DATE, 7 - dayofweek);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(c.getTime());
    }

    public static Calendar getStartTime(Calendar calendar, int interval) {
        if (calendar == null) {
            return null;
        }
        Calendar fromtime = Calendar.getInstance();
        fromtime.setTimeZone(calendar.getTimeZone());
        fromtime.set(Calendar.MILLISECOND, 0);
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        if (interval == DAILY || interval == HOURLY) {
            fromtime.set(y, m, d, 0, 0, 0);
        } else if (interval == WEEKLY) {
            fromtime.set(y, m, d, 0, 0, 0);
            fromtime.add(Calendar.DATE, 1 + Calendar.SUNDAY - fromtime.get(Calendar.DAY_OF_WEEK));
        } else if (interval == MONTHLY) {
            fromtime.set(y, m, 1, 0, 0, 0);
        } else if (interval == BIWEEKLY) {
            fromtime.set(y, m, d, 0, 0, 0);
            fromtime.add(Calendar.WEEK_OF_YEAR, (-1) * (fromtime.get(Calendar.WEEK_OF_YEAR) + 1) % 2);
            fromtime.add(Calendar.DATE, Calendar.SUNDAY - fromtime.get(Calendar.DAY_OF_WEEK));
        } else if (interval == YEARLY) {
            fromtime.set(y, m, d, 0, 0, 0);
        } else if (interval == QUARTLY) {
            fromtime.set(y, (m / 3) * 3, 1, 0, 0, 0);
        } else if (interval == BIYEARLY) {
            fromtime.set(y, (m / 6) * 6, 1, 0, 0, 0);
        }
        return fromtime;
    }

    public static Calendar getEndTime(Calendar calendar, int interval) {
        if (calendar == null) {
            return null;
        }
        Calendar endtime = Calendar.getInstance();
        endtime.setTimeZone(calendar.getTimeZone());
        endtime.set(Calendar.MILLISECOND, 0);
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        if (interval == DAILY) {
            endtime.set(y, m, d, 0, 0, 0);
            endtime.add(Calendar.DAY_OF_MONTH, 1);
        } else if (interval == WEEKLY) {
            endtime.set(y, m, d, 0, 0, 0);
            endtime.add(Calendar.DATE, 2 + Calendar.SATURDAY - endtime.get(Calendar.DAY_OF_WEEK));
        } else if (interval == MONTHLY) {
            endtime.set(y, m, 1, 0, 0, 0);
            endtime.add(Calendar.MONTH, 1);
        } else if (interval == BIWEEKLY) {
            endtime.set(y, m, d, 0, 0, 0);
            endtime.add(Calendar.WEEK_OF_YEAR, endtime.get(Calendar.WEEK_OF_YEAR) % 2);
            endtime.add(Calendar.DATE, 1 + Calendar.SATURDAY - endtime.get(Calendar.DAY_OF_WEEK));
        } else if (interval == YEARLY) {
            endtime.set(y + 1, m, d, 0, 0, 0);
        } else if (interval == QUARTLY) {
            if (m / 3 == 3) {
                endtime.set(y + 1, 0, 1, 0, 0, 0);
            } else {
                endtime.set(y, (m / 3 + 1) * 3, 1, 0, 0, 0);
            }
        } else if (interval == BIYEARLY) {
            if (m / 6 == 1) {
                endtime.set(y + 1, 0, 1, 0, 0, 0);
            } else {
                endtime.set(y, (m / 6 + 1) * 6, 1, 0, 0, 0);
            }
        }
        return endtime;
    }

    public static String format(long l, String pattern) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(l);
        return format(cal.getTime(), pattern);
    }

    /**
     * 取得昨天
     */
    public static String getSpecifiedDayBefore(String specifiedDay) throws ParseException {
        Calendar c = Calendar.getInstance();
        Date date = new SimpleDateFormat(SHORT_DATE).parse(specifiedDay);
        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day - 1);
        String dayBefore = new SimpleDateFormat(SHORT_DATE).format(c
                .getTime());
        return dayBefore;
    }

    /**
     * 计算两个日期之间的天数,'2013-3-10'与'2013-3-12',返回2
     *
     * @param: time1 yyyy-MM-dd,开始时间
     * @param: time2 yyyy-MM-dd,结束时间
     */
    public static long getQuot(String time1, String time2) throws ParseException {
        long quot = 0;
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

        Date date1 = ft.parse(time1);
        Date date2 = ft.parse(time2);
        quot = date2.getTime() - date1.getTime();
        quot = quot / 1000 / 60 / 60 / 24;

        return quot;
    }

    /**
     * 获取两个时间日期之间相差的月份数,'2013-1-1'与'2013-3-1',返回2
     *
     * @param:starttime--"yyyy-MM-dd"
     * @param:endtime --"yyyy-MM-dd"
     */
    public static int getMonths(String starttime, String endtime) throws ParseException {
        Calendar startCal = DateUtils.parseDateString(starttime,
                SHORT_DATE);
        Calendar endCal = DateUtils.parseDateString(endtime,
                SHORT_DATE);
        Calendar temp = Calendar.getInstance();
        temp.setTime(endCal.getTime());
        temp.add(Calendar.DATE, 1);
        int year = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR);
        int month = endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH);
        if ((startCal.get(Calendar.DATE) == 1)
                && (temp.get(Calendar.DATE) == 1)) {
            return year * 12 + month + 1;
        } else if ((startCal.get(Calendar.DATE) != 1)
                && (temp.get(Calendar.DATE) == 1)) {
            return year * 12 + month;
        } else if ((startCal.get(Calendar.DATE) == 1)
                && (temp.get(Calendar.DATE) != 1)) {
            return year * 12 + month;
        } else {
            return (year * 12 + month - 1) < 0 ? 0 : (year * 12 + month);
        }
    }

    /**
     * 获取yyyy-MM-dd HH:mm:ss格式时间日期
     */
    public static String getFormatDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateFormat = simpleDateFormat.format(new Date());
        return dateFormat;
    }

    public static String getFormatDate1() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
        String dateFormat = simpleDateFormat.format(new Date());
        return dateFormat;
    }

    public static String getWeekFirst(String date) throws ParseException {
        Calendar c = DateUtils.parseDateString(date, "yyyy-MM-dd ");
        int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
        c.add(Calendar.DATE, 1 - dayofweek);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return sdf.format(c.getTime());
    }

    public static String getWeekLast(String date) throws ParseException {
        Calendar c = DateUtils.parseDateString(date, "yyyy-MM-dd ");
        int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
        c.add(Calendar.DATE, 7 - dayofweek);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        return sdf.format(c.getTime());
    }
}
