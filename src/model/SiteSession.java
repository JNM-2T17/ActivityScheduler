package model;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

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
	private boolean timesCollated = false;
	
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

	public TimeRange[] getBlacktimes() {
		System.out.println(timesCollated  + " " + blacktimes.size());
		if( !timesCollated && blacktimes.size() > 0 ) {
			TimeRange[] tr = blacktimes.toArray(new TimeRange[0]);
			Arrays.sort(tr,new Comparator<TimeRange>() {
				@Override
				public int compare(TimeRange arg0, TimeRange arg1) {
					// TODO Auto-generated method stub
					return (int)(arg0.getStartTime().getTime().getTime() - arg1.getStartTime().getTime().getTime());
				}
			});
			ArrayList<TimeRange> newTimes = new ArrayList<TimeRange>();
			
			TimeRange curr = new TimeRange(tr[0].getStartTime(),tr[0].getEndTime());
			
			for(int i = 1; i < tr.length; i++) {
				if(curr.getEndTime().compareTo(tr[i].getStartTime()) >= 0 ) {
					curr.setEndTime(curr.getEndTime().compareTo(tr[i].getEndTime()) >= 0 ? curr.getEndTime() : tr[i].getEndTime());
				} else {
					newTimes.add(curr);
					
					curr = new TimeRange(tr[i].getStartTime(),tr[i].getEndTime());
				}
			}
			newTimes.add(curr);
			blacktimes = newTimes;
			timesCollated = true;
		}
		return blacktimes.toArray(new TimeRange[0]);
	}

	public void setBlacktimes(ArrayList<TimeRange> blacktimes) {
		this.blacktimes = blacktimes;
		timesCollated = false;
	}
	
	public void addBlackTime(Calendar start, Calendar end) {
		this.blacktimes.add(new TimeRange(start,end));
		timesCollated = false;
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
}
