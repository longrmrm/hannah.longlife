package com.hannah.common.util;

import java.util.TimeZone;

public class DateTimeUtil {

	public static final int SECONDS_IN_DAY = 60 * 60 * 24;
	public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;
	public static final long MILLIS_IN_HOUR = 1000L * 60 * 60;
	public static final long MILLIS_IN_MINUTE = 1000L * 60;

	private static long toDay(long millis) {
		return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
	}

	public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
		final long interval = ms1 - ms2;
		return interval < MILLIS_IN_DAY && interval > -1L * MILLIS_IN_DAY && toDay(ms1) == toDay(ms2);
	}

	public static boolean isSameHourOfMillis(final long ms1, final long ms2) {
		final long interval = ms1 - ms2;
		return interval < MILLIS_IN_HOUR && interval > -1L * MILLIS_IN_HOUR
				&& (ms1 / MILLIS_IN_HOUR == ms2 / MILLIS_IN_HOUR);
	}

	public static boolean isSameMinuteOfMillis(final long ms1, final long ms2) {
		final long interval = ms1 - ms2;
		return interval < MILLIS_IN_MINUTE && interval > -1L * MILLIS_IN_MINUTE
				&& (ms1 / MILLIS_IN_MINUTE == ms2 / MILLIS_IN_MINUTE);
	}

	public static boolean isSameSecondOfMillis(final long ms1, final long ms2) {
		final long interval = ms1 - ms2;
		return interval < 1000L && interval > -1000L && (ms1 / 1000L == ms2 / 1000L);
	}

	public static boolean isInDayOfMillis(final long ms1, final long ms2) {
		final long interval = ms1 - ms2;
		return interval < MILLIS_IN_DAY && interval > -1L * MILLIS_IN_DAY;
	}

	public static boolean isInHourOfMillis(final long ms1, final long ms2) {
		final long interval = ms1 - ms2;
		return interval < MILLIS_IN_HOUR && interval > -1L * MILLIS_IN_HOUR;
	}

	public static boolean isInMinuteOfMillis(final long ms1, final long ms2) {
		final long interval = ms1 - ms2;
		return interval < MILLIS_IN_MINUTE && interval > -1L * MILLIS_IN_MINUTE;
	}
}