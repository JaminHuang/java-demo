package com.demo.sdk.util;

import com.demo.sdk.exception.BizException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils extends org.apache.commons.lang.time.DateUtils {

    public static final String FORMAT_DATE_I = "yyyy-MM-dd HH:mm";

    public static final String FORMAT_DATE_II = "yyyy/MM/dd HH:mm";

    public static final String FORMAT_DATE_AND_TIME_I = "yyyy-MM-dd HH:mm:ss";

    public static final String FORMAT_DATE_AND_TIME_II = "yyyy/MM/dd HH:mm:ss";

    public static final String FORMAT_ONLY_DATE_I = "yyyy-MM-dd";

    public static final String FORMAT_ONLY_DATE_II = "yyyy/MM/dd";

    public static final String FORMAT_ONLY_TIME_I = "HH:mm";

    public static final String FORMAT_ONLY_TIME_II = "HH:mm:ss";

    public static String getFormat(String source) {
        if (source == null) {
            throw new BizException("dateformat is null");
        }

        String format = null;
        if (source.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$")) {
            format = FORMAT_DATE_AND_TIME_I;
        } else if (source.matches("^\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}:\\d{2}$")) {
            format = FORMAT_DATE_AND_TIME_II;
        } else if (source.matches("^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}$")) {
            format = FORMAT_DATE_I;
        } else if (source.matches("^\\d{4}/\\d{2}/\\d{2} \\d{2}:\\d{2}$")) {
            format = FORMAT_DATE_II;
        } else if (source.matches("^\\d{4}/\\d{2}/\\d{2}$")) {
            format = FORMAT_ONLY_DATE_II;
        } else if (source.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            format = FORMAT_ONLY_DATE_I;
        } else if (source.matches("^\\d{2}:\\d{2}$")) {
            format = FORMAT_ONLY_TIME_I;
        } else if (source.matches("^\\d{2}:\\d{2}:\\d{2}$")) {
            format = FORMAT_ONLY_TIME_II;
        } else {
            throw new BizException("not support this dateformat");
        }
        return format;
    }

    /**
     * 获取下一天
     *
     * @param date
     * @return
     */
    public static Date getNextDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * 获取下一天
     *
     * @param date
     * @return
     */
    public static Date getNextDate(Date date, int interval) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, interval);
        return cal.getTime();
    }

    /**
     * 只格式化日期
     *
     * @param date
     * @return
     */
    public static String formatOnlyDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_ONLY_DATE_I);
        return sdf.format(date);
    }

    /**
     * 格式化字符串 yyyy-MM-dd HH:mm
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date, String format) {
        if (date == null || StringUtils.isEmpty(format)) {

            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 格式化字符串 MM月dd日 HH:mm
     *
     * @param date
     * @return
     */
    public static String formatTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
        String dateStr = sdf.format(date);
        if (dateStr.startsWith("0")) {
            return dateStr.substring(1);
        }
        return dateStr;
    }

    /**
     * 只格式化时间
     *
     * @param date
     * @return
     */
    public static String formatOnlyTime(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    /**
     * 转化时分
     *
     * @param date
     * @return
     */
    public static String formatHourAndMinute(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }

    /**
     * 通过生日日期计算年龄（周岁算法）：用当年年份-生日年份，若若生日的月份和日数 > 当年的月份和日数，则减1
     *
     * @param birDate 生日日期
     * @return int年龄
     */
    public static int getAgeByBirDate(Date birDate) {
        if (birDate == null) {
            return 0;
        }

        int age = 0;
        try {
            if (birDate.after(new Date())) {
                return 0;
            }

            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间

            Calendar birth = Calendar.getInstance();
            birth.setTime(birDate);// 生日日期

            // 年龄为虚岁，用当年年份-生日年份
            age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

            birth.set(Calendar.YEAR, now.get(Calendar.YEAR));
            now.set(Calendar.HOUR_OF_DAY, birth.get(Calendar.HOUR_OF_DAY));
            now.set(Calendar.MINUTE, birth.get(Calendar.MINUTE));
            now.set(Calendar.SECOND, birth.get(Calendar.SECOND));
            now.set(Calendar.MILLISECOND, birth.get(Calendar.MILLISECOND));
            if (birth.after(now)) {// 若生日的月份和日数 > 当年的月份和日数，则减1
                age--;
            }
            return age;
        } catch (Exception e) {// 兼容性更强,异常后返回数据
            return 0;
        }
    }

    /**
     * 通过年龄计算生日日期（周岁算法）：年份减去年龄，默认当前的月份和日数，为生日日期的月份和日数
     *
     * @param age 年龄
     * @return Date 生日日期
     */
    public static Date getBirDateByAge(Integer age) {
        if (age == null) {
            return null;
        }

        Date birDate = new Date();
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间

            // 通过年龄计算生日日期，年份减去年龄，默认当前的月份和日数 为生日日期的月份和日数
            now.add(Calendar.YEAR, 0 - age);
            birDate = now.getTime();
            return birDate;
        } catch (Exception e) {// 兼容性更强,异常后返回数据
            return null;
        }
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Date getCurrentTime() {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"));
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        return date;
    }

    /**
     * 格式化日期和时间
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_AND_TIME_I);
        return sdf.format(date);
    }

    /**
     * 根据格式格式化日期
     *
     * @param date
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date parseDate(String date, String pattern) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.parse(date);
    }

    /**
     * 获得某天最小时间 2018-01-01 00:00:00
     *
     * @param date
     * @return
     */
    public static Date getMinTimeOfDay(Date date) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()),
                ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获得某天最大时间 2018-01-01 23:59:59
     *
     * @param date
     * @return
     */
    public static Date getMaxTimeOfDay(Date date) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()),
                ZoneId.systemDefault());
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 获取两个时间间隔天数
     *
     * @param oldDate
     * @param newDate
     * @return
     */
    public static Long getDateInterval(Date oldDate, Date newDate) {
        if (oldDate == null || newDate == null) {
            return null;
        }
        Calendar oldCal = Calendar.getInstance();
        oldCal.setTime(oldDate);
        LocalDate oldLocalDate = LocalDate.of(oldCal.get(Calendar.YEAR), oldCal.get(Calendar.MONTH) + 1,
                oldCal.get(Calendar.DAY_OF_MONTH));

        Calendar newCal = Calendar.getInstance();
        newCal.setTime(newDate);
        LocalDate newLocalDate = LocalDate.of(newCal.get(Calendar.YEAR), newCal.get(Calendar.MONTH) + 1,
                newCal.get(Calendar.DAY_OF_MONTH));

        return ChronoUnit.DAYS.between(oldLocalDate, newLocalDate);
    }

    /**
     * 根据学生出生日期计算学龄
     *
     * @param birDate
     * @return
     */
    public static int getStudyAgeByBirDate(Date birDate) {
        int age = 0;
        try {
            if (birDate.after(new Date())) {
                return 0;
            }

            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间
            // 把月份设置为9月1号，8表示9月
            now.set(Calendar.MONTH, 8);
            now.set(Calendar.DAY_OF_MONTH, 1);

            Calendar birth = Calendar.getInstance();
            birth.setTime(birDate);// 生日日期

            // 年龄为虚岁，用当年年份-生日年份
            age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

            birth.set(Calendar.YEAR, now.get(Calendar.YEAR));
            now.set(Calendar.HOUR_OF_DAY, birth.get(Calendar.HOUR_OF_DAY));
            now.set(Calendar.MINUTE, birth.get(Calendar.MINUTE));
            now.set(Calendar.SECOND, birth.get(Calendar.SECOND));
            now.set(Calendar.MILLISECOND, birth.get(Calendar.MILLISECOND));
            if (birth.after(now)) {// 若生日的月份和日数 > 当年的月份和日数，则减1
                age--;
            }
            return age;
        } catch (Exception e) {// 兼容性更强,异常后返回数据
            return 0;
        }
    }

    /**
     * 获得今天所在周的周一日期
     *
     * @return
     */
    public static Date getThisMondayDate() {
        return getThisMondayDate(new Date());
    }

    /**
     * 获得date所在周的周一日期
     *
     * @return
     */
    public static Date getThisMondayDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // 国外的习惯是周日是一周的第一天 如果今天是星期天 那么 国内 的定义 这周的星期一是六天前 而国外是第二天
        int days = 0;
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            days = -6;
        } else {
            days = Calendar.MONDAY - calendar.get(Calendar.DAY_OF_WEEK);
        }
        calendar.add(Calendar.DATE, days);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 返回指定天数的日期
     *
     * @param date
     * @param day
     * @return
     */
    public static Date getExpireDate(Date date, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }

    /**
     * <p>Title: getExpireHour</p>
     * <p>Description: 返回指定日期多少小时后的时间</p>
     *
     * @param date
     * @param hour
     * @return
     * @author yql
     */
    public static Date getExpireHour(Date date, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, hour);
        return cal.getTime();
    }

    /**
     * 返回指定日期多少秒后的时间
     * @param date
     * @param second
     * @return
     */
    public static Date getExpireSecond(Date date, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, second);
        return cal.getTime();
    }

    /**
     * 日期 时间拼成 date类型
     *
     * @param date yyyy-MM-dd
     * @param time HH:mm
     * @return
     */
    public static Date toDate(Date date, Date time) {
        String dateStr = formatDate(date, 0);
        String timeStr = formatDate(time, 20);
        String dateTimeStr = dateStr + " " + timeStr;

        return toDate(dateTimeStr, 21);

    }

    /**
     * 根据指定参数kind，获取指定类型的Date日期(年月日)
     *
     * @param kind 指定参数
     * @return Date 指定类型的Date
     */
    public static Date getFormatDate(Date date, int kind) {
        String currentDateStr = formatDate(date, kind);
        return toDate(currentDateStr, kind);
    }

    /**
     * 根据kind输出string格式
     *
     * @param date
     * @param kind
     * @return
     */
    public static String formatDate(Date date, int kind) {
        SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat(kind));
        return sdf.format(date);
    }

    public static String formatCurrentDate(int kind) {
        SimpleDateFormat sdf = new SimpleDateFormat(getDateFormat(kind));
        return sdf.format(new Date());
    }

    public static Date toDate(String dateText, int kind) {
        String format = getDateFormat(kind);
        if (dateText == null) {
            return null;
        }
        DateFormat df = null;
        try {
            if (format == null) {
                df = new SimpleDateFormat();
            } else {
                df = new SimpleDateFormat(format);
            }
            df.setLenient(false);
            return df.parse(dateText);
        } catch (ParseException e) {
            return null;
        }
    }

    private static String getDateFormat(int kind) {
        String[] format = {"yyyy-MM-dd", // 0
                "yyyy-MM-dd HH:mm:ss", // 1
                "yyyy",// 2
                "M",// 3
                "dd", // 4
                "yyyy年M月d日H点m分", // 5
                "yyyy年M月d日", // 6
                "H点m分", // 7
                "yyyy/MM/dd HH:mm", // 8
                "HH",// 9
                "mm",// 10
                "yyyyMMdd", // 11
                "yyyyMMddHHmmss", // 12
                "yyyy-MM-dd 23:59:59", // 13
                "HH:mm:ss", // 14
                "yyyy/MM/dd HH:mm:ss", // 15
                "yyyy/MM/dd HH:mm",//16
                "HHmmss",//17,
                "HH:mm:ss", //18
                "mmss", //19
                "HH:mm", //20
                "yyyy-MM-dd HH:mm", //21
                "yyyyMM" //22
        };
        return format[kind];
    }

    /**
     * 计算两个日期的秒数之差
     *
     * @param oldDate
     * @param newDate
     * @return
     */
    public static long secondsBetween(Date oldDate, Date newDate) {
        long between = (newDate.getTime() / 1000 - oldDate.getTime() / 1000);//除以1000是为了转换成秒
        return between;
    }

    /**
     * 计算传入时间距离24点剩余秒数
     *
     * @param currentDate
     * @return
     */
    public static Integer getRemainSecondsOneDay(Date currentDate) {
        LocalDateTime midnight = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault()).plusDays(1).withHour(0).withMinute(0)
                .withSecond(0).withNano(0);
        LocalDateTime currentDateTime = LocalDateTime.ofInstant(currentDate.toInstant(),
                ZoneId.systemDefault());
        long seconds = ChronoUnit.SECONDS.between(currentDateTime, midnight);
        return (int) seconds;
    }


    /**
     * 返回指定月数的日期
     *
     * @param date
     * @param month
     * @return
     * @author guos
     * @date 2019/4/13 16:26
     **/
    public static Date getExpireMonth(Date date, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, month);
        return cal.getTime();
    }


    /**
     * 校验日期是否合法
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param interval  日期间隔月份 ：-1,导出所有
     * @return
     * @author guos
     * @date 2019/4/13 16:26
     **/
    public static void checkDate(Date startDate, Date endDate, int interval) {

        if (interval == -1) {
            return;
        }
        if (interval < 0) {
            throw new BizException("日期间隔不能小于0");
        }
        if (startDate.after(endDate)) {
            throw new BizException("开始日期不能晚于结束日期");
        }
        if (getExpireMonth(startDate, interval).before(endDate)) {
            throw new BizException("因数据量关系，仅能导出" + interval + "个月的数据");
        }
    }

    /**
     * 洛杉矶时间转北京时间
     *
     * @param UTCStr
     * @param format
     * @throws ParseException
     */
    public static Date GMTToCST(String UTCStr, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = sdf.parse(UTCStr);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR) + 8);
        return calendar.getTime();
    }

    /**
     * 精确到秒
     *
     * @param date
     * @param time
     * @return
     */
    public static Date toDateSecond(Date date, Date time) {
        String dateStr = formatDate(date, 0);
        String timeStr = formatDate(time, 14);
        String dateTimeStr = dateStr + " " + timeStr;
        return toDate(dateTimeStr, 1);
    }

	/**
	 * 返回提现天数的提现日期
	 *
	 * @param date		起始时间
	 * @param day		提现冻结天数
	 * @return		可提现的日期 yyyy-MM-dd
	 */
	public static Date getWithdrawDate(Date date, int day) {
		// 提现日期根据提现天数延迟一天
		int withdrawDay = day + 1;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, withdrawDay);
		String currentDateStr = formatDate(cal.getTime(), 0);
		return toDate(currentDateStr, 0);
	}

	/**
	 * 返回添加or减去minute分钟后的时间
	 *
	 * @param date		起始时间
	 * @param minute		添加or减去多少分钟
	 * @return		处理分钟后的时间
	 */
	public static Date getExpireMinute(Date date, int minute) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minute);
		return cal.getTime();
	}

    /**
     * 判断两个时间段的交集
     *
     * @param leftStartDate
     * @param leftEndDate
     * @param rightStartDate
     * @param rightEndDate
     * @return
     * @author guos
     * @date 2019/7/16 14:57
     **/
    public static boolean isDateCross(Date leftStartDate, Date leftEndDate, Date rightStartDate, Date rightEndDate) {
        return
                ((leftStartDate.getTime() >= rightStartDate.getTime())
                        && leftStartDate.getTime() < rightEndDate.getTime())
                        ||
                        ((leftStartDate.getTime() > rightStartDate.getTime())
                                && leftStartDate.getTime() <= rightEndDate.getTime())
                        ||
                        ((rightStartDate.getTime() >= leftStartDate.getTime())
                                && rightStartDate.getTime() < leftEndDate.getTime())
                        ||
                        ((rightStartDate.getTime() > leftStartDate.getTime())
                                && rightStartDate.getTime() <= leftEndDate.getTime());
    }

}
