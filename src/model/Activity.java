package model;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import model.TimeRange;
import dao.SessionManager;

public class Activity {
	// Immutable values
	private int id;
	private String name;						 // Name of the activity
	private int length;							 // Length of the activity (in minutes)
	private ArrayList<Calendar> dateRange;			 // Possible dates the activity can be scheduled on
	private boolean[] days;
	private Calendar startTimeRange;				 // Start of range of possible times the activity can be scheduled on
	private Calendar endTimeRange;				 	 // End of range of possible times the activity can be scheduled on
	private ArrayList<TargetGroup> targetGroups; 	 // Target groups of the activity
	private Venue venue;						 // Venue of the activity
	private SiteSession session;
	private Calendar[] allDates;
	private ArrayList<TimeRange> possibleTimes;
	private SimpleDateFormat allf;
	private SimpleDateFormat stf;
	private SimpleDateFormat sdf;
	
	// Mutable values
	private Calendar startTime;
	
// Constructor
	
	private Activity(int id,String name, int length, ArrayList<Calendar> dateRange,
					 boolean[] days, Calendar startTimeRange, Calendar endTimeRange,
					 ArrayList<TargetGroup> targetGroups, Venue venue,SiteSession session){
		this.id = id;
		this.name = name;
		this.length = length;
		this.dateRange = dateRange;
		this.days = days;
		this.startTimeRange = startTimeRange;
		this.endTimeRange = endTimeRange;
		this.targetGroups = targetGroups;
		this.venue = venue;
		this.session = session;
		this.allDates = null;
		allf = new SimpleDateFormat("MMMM dd, yyyy HH:mm:ss.SSS");
		stf = new SimpleDateFormat("HH:mm:ss.SSS");
		sdf = new SimpleDateFormat("MMMM dd, yyyy");
	}
	
// Builder
	
	public static class Builder{
		// Immutable values
		private int id;
		private String name;						 // Name of the activity
		private int length;							 // Length of the activity (in minutes)
		private ArrayList<Calendar> dateRange;			 // Possible dates the activity can be scheduled on
		private boolean[] days;
		private Calendar startTimeRange;				 // Start of range of possible times the activity can be scheduled on
		private Calendar endTimeRange;				 	 // End of range of possible times the activity can be scheduled on
		private ArrayList<TargetGroup> targetGroups; 	 // Target groups of the activity
		private Venue venue;						 // Venue of the activity
		private Calendar startTime;
		private SiteSession session;
		
		public Builder(int id,String name, int length, String days, Calendar startTimeRange,
							   Calendar endTimeRange, Venue venue,SiteSession session){
			this.id = id;
			this.name = name;
			this.length = length;
			String[] parts = days.split(",");
			this.days = new boolean[parts.length];
			for(int i = 0; i < parts.length; i++) {
				this.days[i] = parts[i].equals("1");
			}
			this.startTimeRange = startTimeRange;
			this.endTimeRange = endTimeRange;
			this.venue = venue;
			this.session = session;
			dateRange = new ArrayList<Calendar>();
			targetGroups = new ArrayList<TargetGroup>();
		}
		
		public Builder setStartTime(Calendar startTime) {
			this.startTime = startTime;
			
			return this;
		}
		
		public Builder addDate(Calendar date){
			dateRange.add(date);
			return this;
		}
		
		public Builder addTargetGroup(TargetGroup targetGroup){
			targetGroups.add(targetGroup);
			return this;
		}
		
		public Activity buildActivity(){
			Activity a = new Activity(id,name, length, dateRange, days, startTimeRange, endTimeRange, targetGroups, venue,session);
			a.setStartTime(startTime);
			return a;
		}
	}
	
// Getters
	
	public String getName(){
		return name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getLength(){
		return length;
	}
	
	public Calendar[] getDateRange(){
		return dateRange.toArray(new Calendar[0]);
	}
	
	public Calendar[] getAllDates() {
		if( allDates == null ) {
			ArrayList<Calendar> dates = new ArrayList<Calendar>();
			for(Calendar c : dateRange) {
				if( c.compareTo(session.getStartDate()) >= 0 && c.compareTo(session.getEndDate()) <= 0 )
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				dates.add(c);
			}
			//for each day
			for(int i = 0; i < days.length; i++) {
				//if activity can be on that day
				if( days[i] && !session.getBlackDays()[i] ) {
					Calendar c = Calendar.getInstance();
					c.setTime(session.getStartDate().getTime());
					int dayIndex = i + 1;
					while(c.get(Calendar.DAY_OF_WEEK) != dayIndex) {
						c.add(Calendar.DAY_OF_MONTH, 1);
					}
					
					//while before end date
					while(c.compareTo(session.getEndDate()) <= 0) {
						boolean found = false;
						for(Calendar x : dates) {
							if( x.compareTo(c) == 0 ) {
								found = true;
								break;
							}
						}
						if( !found ) {
							dates.add(CalendarFactory.createCalendar(c.getTime().getTime()));
						}
						c.add(Calendar.WEEK_OF_MONTH, 1);
					}
				}
			}
			allDates = dates.toArray(new Calendar[0]);
		}
		return allDates;
	}
	
	public Calendar getStartTimeRange(){
		return startTimeRange;
	}
	
	public Calendar getEndTimeRange(){
		return endTimeRange;
	}
	
	public ArrayList<TargetGroup> getTargetGroups(){
		return targetGroups;
	}
	
	public Venue getVenue(){
		return venue;
	}
	
	public boolean[] getDays() {
		return days;
	}
	
	public String[] getDaysString() {
		String[] days = new String[] {
				"Sunday",
				"Monday",
				"Tuesday",
				"Wednesday",
				"Thursday",
				"Friday",
				"Saturday"
		};
		ArrayList<String> list = new ArrayList<String>();
		for(int i = 0; i < this.days.length; i++ ) {
			if( this.days[i] ) {
				list.add(days[i]);
			}
		}
		return list.toArray(new String[0]);
	}

	public void setDays(boolean[] days) {
		this.days = days;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public void setDateRange(ArrayList<Calendar> dateRange) {
		this.dateRange = dateRange;
	}

	public void setStartTimeRange(Calendar startTimeRange) {
		this.startTimeRange = startTimeRange;
	}

	public void setEndTimeRange(Calendar endTimeRange) {
		this.endTimeRange = endTimeRange;
	}

	public void setTargetGroups(ArrayList<TargetGroup> targetGroups) {
		this.targetGroups = targetGroups;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	public Calendar getStartTime(){
		return startTime;
	}
	
	public Calendar getEndTime(){
		return CalendarFactory.createCalendar(getStartTime().getTime().getTime() + getLength() * 60000);
	}
	
	public boolean isConflict(Activity anotherActivity) {
		return anotherActivity.getStartTime().compareTo(getEndTime()) < 0;
	}
	
// Setters
	
	public void setStartTime(Calendar startTime){
		this.startTime = startTime;
	}
	
	
	
	public SiteSession getSession() {
		return session;
	}

	public void setSession(SiteSession session) {
		this.session = session;
	}

	// set date as random date from the date range
	public void randomizeTime() {
		int dateCtr = getAllDates().length;
		if( dateCtr == 0 ) {
			startTime = CalendarFactory.createCalendar(0);
			System.out.println(allf.format(startTime.getTime()));
			return;
		}
		int randIndex = (int)(Math.random() * dateCtr);
		long dateMillis = getAllDates()[randIndex].getTimeInMillis();
		
//		for(Calendar c : allDates ) {
//			System.out.println(allf.format(c.getTime()));
//		}
		
		if( possibleTimes == null ) {
			TimeRange[] blacktimes = session.getBlacktimes();
			
			possibleTimes = new ArrayList<TimeRange>();
			TimeRange curr = new TimeRange(startTimeRange,endTimeRange);
			
			for(TimeRange tr: blacktimes) {
//				System.out.println(tr);
				if(tr.getStartTime().compareTo(curr.getStartTime()) < 0 ) {
					if( tr.getEndTime().compareTo(curr.getStartTime()) > 0 ) {
						curr.setStartTime(tr.getEndTime());
					}
				} else if( tr.getStartTime().compareTo(curr.getEndTime()) < 0 ){
					if( tr.getEndTime().compareTo(curr.getEndTime()) < 0 ) {
						TimeRange newRange = new TimeRange(tr.getEndTime(),curr.getEndTime());
						curr.setEndTime(tr.getStartTime());
						if( curr.getLength() >= length * 60000 ) {
							possibleTimes.add(curr);
						}
						curr = newRange;
					} else {
						curr.setEndTime(tr.getStartTime());
					}
				}
			}
			if( curr.getLength() >= length * 60000 ) {
				possibleTimes.add(curr);
			}
		}
		
		if(possibleTimes.size() == 0 ) {
			startTime = CalendarFactory.createCalendar(0);
			System.out.println(allf.format(startTime.getTime()));
			return;
		}
		
		long totalTime = 0;
//		System.out.println(name);
		for(TimeRange tr : possibleTimes ) {
			totalTime += tr.getLength();
//			System.out.println("POSSIBLE TIMES: " + tr + " of length " + (tr.getLength() - length * 60000));
		}
		long finalTime = (long)(Math.random() * totalTime);
		
//		System.out.println("Final Time: " + finalTime);
		int i = 0;
		boolean adjusted = false;
		for(; i < possibleTimes.size(); i++) {
			if( finalTime >= 0 ) {
				finalTime -= possibleTimes.get(i).getLength();
			} else {
				i--;
				finalTime += possibleTimes.get(i).getLength();
				adjusted = true;
				break;
			}
		}
		if( !adjusted ) {
			i--;
			finalTime += possibleTimes.get(i).getLength();
		}
		
		TimeRange selectedChunk = possibleTimes.get(i);
		
		long totalChunks = (selectedChunk.getLength() - length * 60000) / 15 / 60 / 1000;
		long timeMillis = (long)(Math.random() * totalChunks) * 15 * 60 * 1000 + selectedChunk.getStartTime().getTimeInMillis();
		
//		System.out.println("SELECTED PERIOD: " + possibleTimes.get(i));
		Calendar time = CalendarFactory.createCalendar(timeMillis);
//		System.out.println("SELECTED TIME: " + stf.format(time.getTime()));
		Calendar date = CalendarFactory.createCalendar(dateMillis);
		date.set(Calendar.HOUR_OF_DAY,time.get(Calendar.HOUR_OF_DAY));
		date.set(Calendar.MINUTE,time.get(Calendar.MINUTE));
		date.set(Calendar.MINUTE,time.get(Calendar.MINUTE));
		setStartTime(date);
	}
	
// Comparison
	
	public boolean hasConflictingTargetGroups(Activity compare){
		for(TargetGroup tg1 : targetGroups) {
			for(TargetGroup tg2 : compare.targetGroups) {
				if( tg1.getId() == tg2.getId() ) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Activity copy() {
		Builder ab = new Builder(id,name, length, SessionManager.stringifyDays(days), startTimeRange,endTimeRange,venue,session);
		for(Calendar d: dateRange) {
			ab.addDate(d);
		}
		for(TargetGroup t : targetGroups) {
			ab.addTargetGroup(t);
		}
		ab.setStartTime(startTime);
		Activity copy = ab.buildActivity();
		copy.possibleTimes = possibleTimes;
		return copy;
	}
	
	public String toString() {
		String ret = name + "\n" + length + " minutes\nAllowed Days: ";
		String[] days = new String[] {
				"Sunday",
				"Monday",
				"Tuesday",
				"Wednesday",
				"Thursday",
				"Friday",
				"Saturday"
		};
		
		String bd = "";
		for(int i = 0; i < this.days.length; i++ ) {
			if( this.days[i] ) {
				bd += "\n" + days[i];
			}
		}
		
		ret += bd + "\n";
		
		ret += "From " + stf.format(startTimeRange.getTime()) + " to " + stf.format(endTimeRange.getTime()) + "\n";
		ret += "Venue:\t" + venue.getName();
		if( targetGroups.size() > 0 ) {
			ret += "\nTarget Groups: ";
			for(TargetGroup tg : targetGroups) {
				ret += "\n" + tg.getName();
			}
		}
		if( dateRange.size() > 0 ) {
			ret += "\nSpecial Dates:";
			for(Calendar c : dateRange ) {
				ret += "\n" + sdf.format(c.getTime());
			}
		}
		if( startTime != null ) {
			ret += "\nAssigned Time:\t" + allf.format(startTime.getTime()) + " - " + allf.format(getEndTime().getTime());
		}
		return ret;
	}
}
