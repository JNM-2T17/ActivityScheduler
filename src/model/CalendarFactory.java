package model;

import java.util.Calendar;

public class CalendarFactory {
	public static Calendar createCalendar(long millis) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millis);
		return c;
	}
}
