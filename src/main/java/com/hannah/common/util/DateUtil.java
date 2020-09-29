package com.hannah.common.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author longrm
 * @date 2012-3-30
 */
public class DateUtil {

	public final static DateFormat ddFormat = new SimpleDateFormat("yyyy-MM-dd");

	public final static DateFormat ddChFormat = new SimpleDateFormat("yyyy年M月d日");

	public final static DateFormat ddShortFormat = new SimpleDateFormat("yyyyMMdd");

	public final static DateFormat ssFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public final static DateFormat utcFormat = new SimpleDateFormat("EEE, d-MMM-yyyy HH:mm:ss z", Locale.ENGLISH);
	
	public static void setTimeZone(TimeZone zone) {
		ddFormat.setTimeZone(zone);
		ddChFormat.setTimeZone(zone);
		ddShortFormat.setTimeZone(zone);
		ssFormat.setTimeZone(zone);
	}

	/**
	 * @param date
	 * @return yyyy-MM-dd
	 */
	public static String dateToDdString(Date date) {
		if (date == null) {
			return null;
		}
		return ddFormat.format(date);
	}

	/**
	 * @param date
	 * @return yyyy年M月d日
	 */
	public static String dateToDdChString(Date date) {
		if (date == null) {
			return null;
		}
		return ddChFormat.format(date);
	}

	/**
	 * @param date
	 * @return yyyyMMdd
	 */
	public static String dateToDdShortString(Date date) {
		return ddShortFormat.format(date);
	}

	/**
	 * @param date
	 * @return yyyy-MM-dd HH:mm:ss
	 */
	public static String dateToSsString(Date date) {
		return ssFormat.format(date);
	}

	/**
	 * @param dateStr
	 *            yyyy-MM-dd
	 * @return
	 */
	public static Date ddStringToDate(String dateStr) {
		Date date = null;
		try {
			date = ddFormat.parse(dateStr);
		} catch (ParseException pe) {
			throw new RuntimeException(pe.getMessage(), pe);
		}
		return date;
	}

	/**
	 * @param dateStr
	 *            yyyy年M月d日
	 * @return
	 */
	public static Date ddChStringToDate(String dateStr) {
		Date date = null;
		try {
			date = ddChFormat.parse(dateStr);
		} catch (ParseException pe) {
			throw new RuntimeException(pe.getMessage(), pe);
		}
		return date;
	}

	/**
	 * @param dateStr
	 *            yyyyMMdd
	 * @return
	 */
	public static Date ddShortStringToDate(String dateStr) {
		Date date = null;
		try {
			date = ddShortFormat.parse(dateStr);
		} catch (ParseException pe) {
			throw new RuntimeException(pe.getMessage(), pe);
		}
		return date;
	}

	/**
	 * @param dateStr
	 *            yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static Date ssStringToDate(String dateStr) {
		Date date = null;
		try {
			date = ssFormat.parse(dateStr);
		} catch (ParseException pe) {
			throw new RuntimeException(pe.getMessage(), pe);
		}
		return date;
	}

	/**
	 * @param year
	 * @param month
	 * @return Date
	 */
	public static Date getFirstDayOfMonth(int year, int month) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	/**
	 * @param year
	 * @param month
	 * @return
	 */
	public static Date getLastDayOfMonth(int year, int month) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	public static int getYear(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c.get(Calendar.YEAR);
	}

	public static int getMonth(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c.get(Calendar.MONTH) + 1;
	}

	public static int getDayOfMonth(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public static int getHourOfDay(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c.get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinute(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return c.get(Calendar.MINUTE);
	}

	/**
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static int getDaysBetween(Date startDate, Date endDate) {
		Calendar startC = Calendar.getInstance();
		Calendar endC = Calendar.getInstance();
		startC.setTime(startDate);
		endC.setTime(endDate);
		return endC.get(Calendar.DAY_OF_YEAR) - startC.get(Calendar.DAY_OF_YEAR);
	}

	public static Date parseUtcDate(String utc) {
		try {
			return utcFormat.parse(utc);
		} catch (ParseException pe) {
			throw new RuntimeException(pe.getMessage(), pe);
		}
	}

}
