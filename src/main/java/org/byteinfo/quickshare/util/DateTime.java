package org.byteinfo.quickshare.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public interface DateTime {
	// 2007-12-03
	static String formatDate(long millis) {
		return getLocalDateTime(millis).toLocalDate().toString();
	}

	// 12-03
	static String formatShortDate(long millis) {
		return MonthDay.from(getLocalDateTime(millis)).toString().replace("--", "");
	}

	// 10:15:30
	static String formatTime(long millis) {
		return getLocalDateTime(millis).toLocalTime().toString();
	}

	// 2007-12-03 10:15:30
	static String formatDateTime(long millis) {
		return getLocalDateTime(millis).toString().replace('T', ' ');
	}

	// 2007-12-03 if not today
	// 10:15:30 if today
	static String formatDateOrTime(long millis) {
		var localDateTime = getLocalDateTime(millis);
		if (LocalDate.now().equals(localDateTime.toLocalDate())) {
			return localDateTime.toLocalTime().toString();
		} else {
			return localDateTime.toLocalDate().toString();
		}
	}

	// 12-03 if not today
	// 10:15:30 if today
	static String formatShortDateOrTime(long millis) {
		var localDateTime = getLocalDateTime(millis);
		if (LocalDate.now().equals(localDateTime.toLocalDate())) {
			return localDateTime.toLocalTime().toString();
		} else {
			return MonthDay.from(localDateTime).toString().replace("--", "");
		}
	}

	private static LocalDateTime getLocalDateTime(long millis) {
		return Instant.ofEpochMilli(millis).truncatedTo(ChronoUnit.SECONDS).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
}
