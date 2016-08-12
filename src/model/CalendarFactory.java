package model;

import java.util.Calendar;

public class CalendarFactory {
	public static Calendar createCalendar(long millis) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millis);
		return c;
	}
	
	public static Calendar createCalendar(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(year, month, day);
		return c;
	}
	
	public static Calendar createCalendarTime(int hour, int minute, int second) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(Calendar.HOUR_OF_DAY,hour);
		c.set(Calendar.MINUTE,minute);
		c.set(Calendar.SECOND, second);
		return c;
	}
	
	public static Calendar createCalendar(String mmddyyyy) {
		String[] parts = mmddyyyy.split("[/]");
		return createCalendar(Integer.parseInt(parts[2]), Integer.parseInt(parts[0]) - 1, Integer.parseInt(parts[1]));
	}
	
	public static Calendar createCalendarTime(String hhmm) {
		int time = Integer.parseInt(hhmm);
		return createCalendarTime(time/100, time % 100, 0);
	}
}
