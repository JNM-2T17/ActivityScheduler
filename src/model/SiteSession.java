package model;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SiteSession {
	private int id;
	private String name;
	private boolean[] blackDays;
	private Calendar startDate;
	private Calendar endDate;
	private ArrayList<Calendar> blackdates;
	private ArrayList<TimeRange> blacktimes;
	private SimpleDateFormat allf;
	private SimpleDateFormat sdf;
	private SimpleDateFormat stf;
	
	public SiteSession() {
		super();
		allf = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss.SSS");
		sdf = new SimpleDateFormat("MMMM dd, yyyy");
		stf = new SimpleDateFormat("HH:mm:ss.SSS");
	}

	public SiteSession(int id, String name, String blackDays, Calendar startDate, Calendar endDate) {
		super();
		this.id = id;
		this.name = name;
		parseDays(blackDays);
		this.startDate = startDate;
		this.endDate = endDate;
		this.blackdates = new ArrayList<Calendar>();
		this.blacktimes = new ArrayList<TimeRange>();
		allf = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss.SSS");
		sdf = new SimpleDateFormat("MMMM dd, yyyy");
		stf = new SimpleDateFormat("HH:mm:ss.SSS");
	}

	private void parseDays(String blackDays) {
		String[] parts = blackDays.split(",");
		this.blackDays = new boolean[parts.length];
		for(int i = 0; i < this.blackDays.length; i++) {
			this.blackDays[i] = parts[i].equals("1");
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean[] getBlackDays() {
		return blackDays;
	}

	public void setBlackDays(boolean[] blackDays) {
		this.blackDays = blackDays;
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
	
	public String toString() {
		String ret = "ID: " + id + "\nName: " + name + "\nStart Date: " + 
					sdf.format(startDate.getTime()) + "\nEnd Date: " + sdf.format(endDate.getTime()); 
		String bd = "\nBlack Days:\n";
		String[] days = new String[] {
				"Sunday",
				"Monday",
				"Tuesday",
				"Wednesday",
				"Thursday",
				"Friday",
				"Saturday"
		};
		for(int i = 0; i < blackDays.length; i++ ) {
			if( blackDays[i] ) {
				bd += days[i] + "\n";
			}
		}
		
		if( bd.length() > 13 ) {
			ret += bd;
		}
		if(blacktimes.size() > 0 ) {
			ret += "\nBlack Times: ";
			for(TimeRange tr : blacktimes) {
				ret += "\n" + tr.toString();
			}
		}
		
		if( blackdates.size() > 0 ) {
			ret += "\nBlack Dates: ";
			for(Calendar c : blackdates) {
				ret += "\n" + sdf.format(c.getTime());
			}
		}
		return ret;
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
		
		public String toString() {
			return stf.format(startTime.getTime()) + " to " + stf.format(endTime.getTime());
		}
	}
}
