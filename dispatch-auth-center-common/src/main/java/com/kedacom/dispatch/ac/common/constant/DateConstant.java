package com.kedacom.dispatch.ac.common.constant;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @Description:
 * @Auther: liuyanhui
 * @Date: 2022/04/26/ 17:15
 */
public class DateConstant {
    /**
     *     时间格式
     */
    public static final String SHORT_DATE = "yyyy-MM-dd";
    public static final String SHORT_DATE_ZMS = "yyyyMMdd";
    public static final String LONG_DATE = "yyyy-MM-dd HH:mm:ss";
    public static final String LONG_DATE_ZMS = "yyyyMMddHHssmm";
    public static final String YMDHM = "yyyy-MM-dd HH:mm";
    public static final String HM = "HH:mm";
    /**
     * 时间模版
     */
    public static final SimpleDateFormat DF_SHORT_CN_ZMS = new SimpleDateFormat(SHORT_DATE_ZMS, Locale.US);
    public static final SimpleDateFormat DF_SHORT_CN = new SimpleDateFormat(SHORT_DATE, Locale.US);
    public static final SimpleDateFormat SDF_YMDHM = new SimpleDateFormat(YMDHM, Locale.US);
    public static final SimpleDateFormat SDF_HM = new SimpleDateFormat(HM, Locale.US);
    public static final SimpleDateFormat DF_CN = new SimpleDateFormat(LONG_DATE, Locale.US);


    public static final int REALTIME = 0;
    public static final int HOURLY = 1;
    public static final int DAILY = 2;
    public static final int BIWEEKLY = 3;
    public static final int WEEKLY = 4;
    public static final int MONTHLY = 5;
    public static final int QUARTLY = 6;
    public static final int BIYEARLY = 7;
    public static final int YEARLY = 8;
}
