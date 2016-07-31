package model;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

public class SiteSession {
	private int id;
	private boolean[] blackDays;
	private Calendar startDate;
	private Calendar endDate;
	private ArrayList<Calendar> blackdates;
	private ArrayList<TimeRange> blacktimes;
	
	public SiteSession() {
		super();
	}

	public SiteSession(int id, Calendar startDate, Calendar endDate) {
		super();
		this.id = id;
		this.startDate = startDate;
		this.endDate = endDate;
		this.blackdates = new ArrayList<Calendar>();
		this.blacktimes = new ArrayList<TimeRange>();
	}

	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Calendar getStartDate() {
		return startDate;
	}

	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}

	public Calendar getEndDate() {
		return endDate;
	}

	public void setEndDate(Calendar endDate) {
		this.endDate = endDate;
	}

	public ArrayList<Calendar> getBlackdates() {
		return blackdates;
	}

	public void setBlackdates(ArrayList<Calendar> blackdates) {
		this.blackdates = blackdates;
	}
	
	public void addBlackdate(Calendar c) {
		blackdates.add(c);
	}

	public ArrayList<TimeRange> getBlacktimes() {
		return blacktimes;
	}

	public void setBlacktimes(ArrayList<TimeRange> blacktimes) {
		this.blacktimes = blacktimes;
	}
	
	public void addBlackTime(Calendar start, Calendar end) {
		this.blacktimes.add(new TimeRange(start,end));
	}

	class TimeRange {
		private Calendar startTime;
		private Calendar endTime;
		public TimeRange(Calendar startTime, Calendar endTime) {
			super();
			this.startTime = startTime;
			this.endTime = endTime;
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
		
		
	}
}
