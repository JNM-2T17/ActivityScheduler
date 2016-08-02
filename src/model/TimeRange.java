package model;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeRange {
	private Calendar startTime;
	private Calendar endTime;
	private SimpleDateFormat stf;
	
	public TimeRange(Calendar startTime, Calendar endTime) {
		super();
		this.startTime = startTime;
		this.endTime = endTime;
		stf = new SimpleDateFormat("HH:mm:ss.SSS");
	}
	public TimeRange() {
		super();
	}
	public Calendar getStartTime() {
		return startTime;
	}
	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}
	public Calendar getEndTime() {
		return endTime;
	}
	public void setEndTime(Calendar endTime) {
		this.endTime = endTime;
	}
	
	public long getLength() {
		return endTime.getTimeInMillis() - startTime.getTimeInMillis();
	}
	
	public boolean isInside(long timeMillis) {
		long start = startTime.getTime().getTime() % 86400000;
		long end = endTime.getTime().getTime() % 86400000;
		return timeMillis >= start && timeMillis <= end;
	}
	
	public String toString() {
		return stf.format(startTime.getTime()) + " to " + stf.format(endTime.getTime());
	}
}
