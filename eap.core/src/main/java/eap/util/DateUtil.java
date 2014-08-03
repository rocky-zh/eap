package eap.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * <p> Title: </p>
 * <p> Description: </p>
 * @作者 chiknin@gmail.com
 * @创建时间 
 * @版本 1.00
 * @修改记录
 * <pre>
 * 版本	   修改人		 修改时间		 修改内容描述
 * ----------------------------------------
 * 
 * ----------------------------------------
 * </pre>
 */
public class DateUtil {
	
	public static String DATE_FORMAT_FULL_STRING = "yyyy-MM-dd HH:mm:ss";
	public static String DATE_FORMAT_DATE_STRING = "yyyy-MM-dd";
	public static String DATE_FORMAT_TIME_STRING = "HH:mm:ss";
	
	public static String DATE_FORMAT_MONTH_STRING = "MM";
	public static String DATE_FORMAT_DAY_STRING = "dd";
	
	/**
	 * 获取当前日期，所有代码中必须通过此方法获取当前日期
	 * @return
	 */
	public static Date currDate() {
		return new Date();
	}
	
	/**
	 * 获取当前日期字符串(yyyy-MM-dd HH:mm:ss)
	 * @return
	 */
	public static String currDateFull() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		
		StringBuilder buf = new StringBuilder();
		buf.append(c.get(Calendar.YEAR));
		buf.append("-");
		buf.append(StringUtils.leftPad(String.valueOf(c.get(Calendar.MONTH) + 1), 2, "0"));
		buf.append("-");
		buf.append(StringUtils.leftPad(String.valueOf(c.get(Calendar.DAY_OF_MONTH)), 2, "0"));
		buf.append(" ");
		buf.append(StringUtils.leftPad(String.valueOf(c.get(Calendar.HOUR_OF_DAY)), 2, "0"));
		buf.append(":");
		buf.append(StringUtils.leftPad(String.valueOf(c.get(Calendar.MINUTE)), 2, "0"));
		buf.append(":");
		buf.append(StringUtils.leftPad(String.valueOf(c.get(Calendar.SECOND)), 2, "0"));
		
		return buf.toString();
//		return format(currDate(), new SimpleDateFormat(DATE_FORMAT_FULL_STRING)); // 0.15ms -> 0.05ms ->
	}
	
	/**
	 * 获取当前月份字符串(MM)
	 * @return
	 */
	public static String currMonth() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		
		return StringUtils.leftPad(String.valueOf(c.get(Calendar.MONTH) + 1), 2, "0");
//		return format(currDate(), new SimpleDateFormat(DATE_FORMAT_MONTH_STRING));
	}
	
	/**
	 * 获取当前日期字符串(dd)
	 * @return
	 */
	public static String currDay() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(System.currentTimeMillis());
		
		return StringUtils.leftPad(String.valueOf(c.get(Calendar.DAY_OF_MONTH)), 2, "0");
//		return format(currDate(), new SimpleDateFormat(DATE_FORMAT_DAY_STRING));
	}
	
	/**
	 * 解析时间字符串(yyyy-MM-dd HH:mm:ss)转换为Date对象
	 * @param source 
	 * @return 
	 * @sample 
	 * <pre>
	 *  1. DateUtil.parseFull('2012-01-12 10:11:12')
	 * </pre>
	 */
	public static Date parseFull(String source) {
		return parse(source, new SimpleDateFormat(DATE_FORMAT_FULL_STRING));
	}
	public static Date parseDate(String source) {
		return parse(source, new SimpleDateFormat(DATE_FORMAT_DATE_STRING));
	}
	public static Date parseTime(String source) {
		return parse(source, new SimpleDateFormat(DATE_FORMAT_TIME_STRING));
	}
	public static Date parse(String source, String pattern) {
		return parse(source, new SimpleDateFormat(pattern));
	}
	public static Date parse(String source, SimpleDateFormat sdf) {
		if (StringUtils.isEmpty(source)) return null;
		try {
			return sdf.parse(source);
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}
	public static Date parse(String source) {
		if (StringUtils.isEmpty(source)) return null;
		
		switch (source.length()) {
			case 19:
				return parseFull(source);
			case 10:
				return parseDate(source);
			case 8:
				return parseTime(source);
			default: 
				throw new IllegalArgumentException("Could not parse date");
		}
	}
	

	public static String formatFull(Date date) {
		return format(date, new SimpleDateFormat(DATE_FORMAT_FULL_STRING));
	}
	public static String formatDate(Date date) {
		return format(date, new SimpleDateFormat(DATE_FORMAT_DATE_STRING));
	}
	public static String formatTime(Date date) {
		return format(date, new SimpleDateFormat(DATE_FORMAT_TIME_STRING));
	}
	public static String format(Date date, String pattern) {
		return format(date, new SimpleDateFormat(pattern));
	}
	public static String format(Date date, DateFormat df) {
		if (date == null) return null;
		return df.format(date);
	}
	public static String format(String srcDate, String srcPattern, String descPattern) {
		return format(parse(srcDate, srcPattern), descPattern);
	}
	public static String format(Date date) {
		if (date == null) return null;
		
		if (String.valueOf(date.getTime()).endsWith("00000")) {
			return format(date, new SimpleDateFormat(DATE_FORMAT_DATE_STRING));
		}
		else if (date.getYear() == 70 && date.getMonth() == 0 && date.getDate() == 1) { // 1970-1-1
			return format(date, new SimpleDateFormat(DATE_FORMAT_TIME_STRING));
		} 
		else {
			return format(date, new SimpleDateFormat(DATE_FORMAT_FULL_STRING));
		}
	}
	
	public static Calendar getCalendar(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		
		return c;
	}

	public static Date offset(Date date, int field, int amount) {
		if (date == null) return null;
		Calendar newDate = getCalendar(date);
		newDate.add(field, amount);

		return newDate.getTime();
	}
	
	public static void offsetMinutes(Calendar date, int amount) {
		date.add(Calendar.MINUTE, amount);
	}
	public static Date offsetMinutes(Date date, int amount) {
		return offset(date, Calendar.MINUTE, amount);
	}
	
	public static Date offsetSeconds(Date date, int amount) {
		return offset(date, Calendar.SECOND, amount);
	}
	
	public static void offsetDays(Calendar date, int amount) {
		date.add(Calendar.DAY_OF_YEAR, amount);
	}
	public static Date offsetDays(Date date, int amount) {
		return offset(date, Calendar.DAY_OF_YEAR, amount);
	}
	
	public static void offsetMonths(Calendar date, int amount) {
		date.add(Calendar.MONTH, amount);
	}
	public static Date offsetMonths(Date date, int amount) {
		return offset(date, Calendar.MONTH, amount);
	}
	
	public static void offsetYears(Calendar date, int amount) {
		date.add(Calendar.YEAR, amount);
	}
	public static Date offsetYears(Date date, int amount) {
		return offset(date, Calendar.YEAR, amount);
	}
	
	public static Date nextAnyYear(Date date, int amount) {
		if (date == null) return null;
		Calendar srcDate = getCalendar(date);
		
		Calendar newDate = getCalendar(date);
		newDate.set(Calendar.YEAR, srcDate.get(Calendar.YEAR) + amount);
		newDate.set(Calendar.MONTH, srcDate.get(Calendar.MONTH));
		newDate.set(Calendar.DAY_OF_MONTH, srcDate.get(Calendar.DAY_OF_MONTH) - 1);

		return newDate.getTime();
	}
	
	public static Date offsetDayToFloor(Date date) {
		if (date == null) {
			return null;
		}
		
		return parseFull(formatDate(date) + " 00:00:00");
	}
	public static Date offsetDayToCeiling(Date date) {
		if (date == null) {
			return null;
		}
		
		return parseFull(formatDate(date) + " 23:59:59");
	}
	
	public static Date offsetMonthToFloor(Date date) {
		if (date == null) {
			return null;
		}
		
		Calendar c = getCalendar(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTime();
	}
	public static Date offsetMonthToCeiling(Date date) {
		if (date == null) {
			return null;
		}
		
		Calendar c = getCalendar(date);
		c.set(Calendar.DAY_OF_MONTH, getLastDayOfMonth(date));
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		
		return c.getTime();
	}

	/**
	 * 判断日期大小
	 * @param srcDate 源日期
	 * @param descDate 目标日期
	 * @return 0: srcDate等于descDate, 1: srcDate大于descDate, -1: srcDate小于descDate
	 */
	public static int compareTo(Date srcDate, Date descDate) {
		Calendar srcDateC = getCalendar(srcDate);
		Calendar descDateC = getCalendar(descDate);

		int srcYear = srcDateC.get(Calendar.YEAR);
		int descYear = descDateC.get(Calendar.YEAR);
		if (srcYear == descYear) {
			int srcDayOfYear = srcDateC.get(Calendar.DAY_OF_YEAR);
			int descDayOfYear = descDateC.get(Calendar.DAY_OF_YEAR);
			if (srcDayOfYear == descDayOfYear) {
				return 0;
			} else {
				return srcDayOfYear > descDayOfYear ? 1 : -1;
			}
		} else {
			return srcYear > descYear ? 1 : -1;
		}
	}
	
	/**
	 * 判断月和日大小
	 * @param srcDate 源日期
	 * @param descDate 目标日期
	 * @return 0: srcDate等于descDate, 1: srcDate大于descDate, -1: srcDate小于descDate
	 */
	public static int compareToMonthAndDay(Date srcDate, Date descDate) {
		return compareToMonthAndDay(srcDate, descDate, false);
	}
	
	/**
	 * 判断月和日大小
	 * @param srcDate 源日期
	 * @param descDate 目标日期
	 * @param 如果srcDate为闰年2.29号， 按2.28号算
	 * @return 0: srcDate等于descDate, 1: srcDate大于descDate, -1: srcDate小于descDate
	 */
	public static int compareToMonthAndDay(Date srcDate, Date descDate, boolean ignoreLeapYear_2_29) {
		Calendar srcDateC = getCalendar(srcDate);
		Calendar descDateC = getCalendar(descDate);
		
		int srcMonth = srcDateC.get(Calendar.MONTH);
		int descMonth = descDateC.get(Calendar.MONTH);
		if (srcMonth ==  descMonth) {
			int srcDayOfMonth = srcDateC.get(Calendar.DAY_OF_MONTH);
			int descDayOfMonth = descDateC.get(Calendar.DAY_OF_MONTH);
			if (srcDayOfMonth == descDayOfMonth) {
				return 0;
			} else {
				if (ignoreLeapYear_2_29) {
					if (srcDateC.get(Calendar.DAY_OF_MONTH) == 29 && !isLeapYear(descDate)) {
						srcDayOfMonth--;
					}
					if (srcDayOfMonth == descDayOfMonth) {
						return 0;
					}
				}
				
				return srcDayOfMonth > descDayOfMonth ? 1 : -1;
			}
		} else {
			return srcMonth > descMonth ? 1 : -1;
		}
	}
	
	public static int getDayOfMonth(Date date) {
		return getCalendar(date).get(Calendar.DAY_OF_MONTH);
	}
	public static int getMonth(Date date) {// 0 - 11
		return getCalendar(date).get(Calendar.MONTH);
	}
	
	public static int getDayOfYear(Date date) {
		return getCalendar(date).get(Calendar.DAY_OF_YEAR);
	}
	public static int getYear(Date date) {
		return getCalendar(date).get(Calendar.YEAR);
	}
	
	public static int getDayOfWeek(Date date) {
		return getCalendar(date).get(Calendar.DAY_OF_WEEK);
	}
	public static int getWeekOfYear(Date date) {
		return getCalendar(date).get(Calendar.WEEK_OF_YEAR);
	}
	
	public static int getQuarter(Date date) {//1 - 4
		return (int) Math.ceil((getCalendar(date).get(Calendar.MONTH) + 1) / 3d);
	}
	
	public static boolean isLeapYear(Date date) {
		int year = getYear(date);
		
		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static int getLastDayOfMonth(Date date) {
		int lastDay = 0;
		int month = getMonth(date);
		switch (month) {
			case 0:
			case 2:
			case 4:
			case 6:
			case 7:
			case 9:
			case 11: lastDay = 31; break;
			case 1: lastDay = (isLeapYear(date) ? 29 : 28); break;
			default: lastDay = 30;
		}
		
		return lastDay;
	}
	
	public static int getLastDayOfYear(Date date) {
		return isLeapYear(date) ? 366 : 365;
	}
	
	public static int getInterval(Date fromDate, Date toDate, int field) {
		Calendar fromDateC = getCalendar(fromDate);
		Calendar toDateC = getCalendar(toDate);
		if (toDateC.before(fromDate)) {
			return 0;
		}
		
		if (Calendar.YEAR == field) {
			return toDateC.get(Calendar.YEAR) - fromDateC.get(Calendar.YEAR);
		} 
		else if (Calendar.MONTH == field) {
			int months = 0;
			int intervalYears = (toDateC.get(Calendar.YEAR) - fromDateC.get(Calendar.YEAR));
			if (intervalYears == 0) {
				months = toDateC.get(Calendar.MONTH) - fromDateC.get(Calendar.MONTH);
			} else {
				int months_h = (11 - fromDateC.get(Calendar.MONTH));
				int months_f = toDateC.get(Calendar.MONTH) + 1;
				int months_m = intervalYears <= 1 ? 0 : ((intervalYears - 1) * 12);
				
				months = (months_h + months_m + months_f);
			}
				
			return months;
		}
		else if (Calendar.DATE == field) {
			int days = 0;
			int intervalYears = (toDateC.get(Calendar.YEAR) - fromDateC.get(Calendar.YEAR));
			if (intervalYears == 0) {
				days = toDateC.get(Calendar.DAY_OF_YEAR) - fromDateC.get(Calendar.DAY_OF_YEAR);
			} else {
				int days_h = getLastDayOfYear(fromDate) - fromDateC.get(Calendar.DAY_OF_YEAR);
				int days_f = toDateC.get(Calendar.DAY_OF_YEAR);
				int days_m = 0;
				if (intervalYears > 1) {
					Date tmpDate = null;
					for (int i = 1; i < intervalYears; i++) {
						tmpDate = offsetYears(fromDate, 1);
						days_m += getLastDayOfYear(tmpDate);
					}
				}
				
				days = (days_h + days_m + days_f);
			}
			
			return days;
		}
		
		return 0;
	}
	
	public static int getIntervalDays(Date fromDate, Date toDate) {
		return getInterval(fromDate, toDate, Calendar.DATE);
	}
	public static int getIntervalMonths(Date fromDate, Date toDate) {
		return getInterval(fromDate, toDate, Calendar.MONTH);
	}
	public static int getIntervalYears(Date fromDate, Date toDate) {
		return getInterval(fromDate, toDate, Calendar.YEAR);
	}
	
	public static int birthdayToAge(Date date) {
		return getIntervalYears(date, currDate());
	}
	
	public static String formatFriendlyDate(Date date) {
		Calendar currC = getCalendar(currDate());
		Calendar dateC = getCalendar(date);
		int cYear = currC.get(Calendar.YEAR);
		int year = dateC.get(Calendar.YEAR);
		switch (cYear - year) {
			case 0:
				break;
			case 1:
				return format(date, "去年M月d日 H:m:s");
			case 2: 
				return format(date, "前年M月d日 H:m:s");
			default:
				return format(date, "yyyy年M月d日 H:m:s");
		}
		
		int cMonth = currC.get(Calendar.MONDAY);
		int month = dateC.get(Calendar.MONDAY);
		switch (cMonth - month) {
			case 0:
				break;
			default:
				return format(date, "M月d日 H:m:s");
		}
		
		int cDay = currC.get(Calendar.DAY_OF_MONTH);
		int day = dateC.get(Calendar.DAY_OF_MONTH);
		switch (cDay - day) {
			case 0:
				break;
			case 1:
				return format(date, "昨天 H:m:s");
			case 2:
				return format(date, "前天 H:m:s");
			default:
				return format(date, "M月d日 H:m:s");
		}
		
		int cHour = currC.get(Calendar.HOUR_OF_DAY);
		int hour = dateC.get(Calendar.HOUR_OF_DAY);
		switch (cHour - hour) {
			case 0:
				break;
			default:
				return format(date, "H:m:s");
		}
		
		int cMinute = currC.get(Calendar.MINUTE);
		int minute = dateC.get(Calendar.MINUTE);
		switch (cMinute - minute) {
			case 0:
				break;
			default:
				return (cMinute - minute) +"分钟之前";
		}
		
		int cSecond = currC.get(Calendar.SECOND);
		int second = dateC.get(Calendar.SECOND);
		switch (cSecond - second) {
			case 0:
				return "刚刚";
			default:
				return (cSecond - second) +"秒之前";
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
//		Executor e = Executors.newFixedThreadPool(120);
//		for (int i = 0; i < 1000000; i++) {
//			e.execute(new Runnable() {
//				public void run() {
//					long s = System.nanoTime();
					System.out.println(DateUtil.currDateFull());
//					System.out.println((System.nanoTime() - s) / 1000000.0);
//				}
//			});
//		}
//		
//		Thread.sleep(100000);
		
		
//		Date d = new Date();
//		
//		System.out.println(format(offsetMonthToFloor(d), "yyyy-MM-dd HH:mm:ss.SSS"));
//		System.out.println(format(offsetMonthToCeiling(d), "yyyy-MM-dd HH:mm:ss.SSS"));
		
//		Date d1 = offsetDays(d, 0);
//		d1 = offsetMonths(d, 8);
////		d1 = offsetYears(d1, 1);
//		Date d2 = parse("20080808", "yyyyMMdd");
		
//		Date d3 = DateUtil.offsetMinutes(d, -60);
//		System.out.println(d3);
//		
//		System.out.println(formatFull(d));
//		System.out.println(formatFull(d1));
		
//		System.out.println(getDayOfYear(d));
//		System.out.println(getMonth(d));
//		System.out.println(getDayOfWeek(d));
//		System.out.println(getWeekOfYear(d));
//		System.out.println(getQuarter(d));
//		System.out.println(getInterval(d, d1, Calendar.YEAR));
//		System.out.println(getInterval(d, d1, Calendar.MONTH));
//		System.out.println(getInterval(d, d1, Calendar.DATE));
//		System.out.println(getCalendar(d).get(Calendar.DATE));
//		System.out.println(birthdayToAge(d2));
		
//		Date d = parse("01:11:12", "HH:mm:ss");
//		System.out.println(d.getTime());
//		System.out.println(format(d));
//		00000
//		000000
//		00000
//		43872000
		
//		System.out.println(new Timestamp(DateUtil.currDate().getTime()));
		
//		Date d5 = parse("2012-02-29");
//		System.out.println(d5);
//		Date d6 = parse("2013-02-28");
//		System.out.println(compareMdTo(d5, d6, true));
		
		
//		System.out.println(formatFriendlyDate(currDate()));
//		System.out.println(formatFriendlyDate(offsetSeconds(currDate(), -11)));
//		System.out.println(formatFriendlyDate(offsetMinutes(currDate(), -300)));
//		System.out.println(formatFriendlyDate(offsetDays(currDate(), -1)));
//		System.out.println(formatFriendlyDate(offsetDays(currDate(), -2)));
//		System.out.println(formatFriendlyDate(offsetYears(currDate(), -1)));
//		System.out.println(formatFriendlyDate(offsetYears(currDate(), -2)));
//		System.out.println(formatFriendlyDate(offsetYears(currDate(), -4)));
	}
}