package model;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class Activity {
	// Immutable values
	private String name;						 // Name of the activity
	private int length;							 // Length of the activity (in minutes)
	private ArrayList<Calendar> dateRange;			 // Possible dates the activity can be scheduled on
	private boolean[] days;
	private Time startTimeRange;				 // Start of range of possible times the activity can be scheduled on
	private Time endTimeRange;				 	 // End of range of possible times the activity can be scheduled on
	private ArrayList<TargetGroup> targetGroups; 	 // Target groups of the activity
	private Venue venue;						 // Venue of the activity
	private SiteSession session;
	private Calendar[] allDates;
	
	// Mutable values
	private Calendar date;
	private Time startTime;
	
// Constructor
	
	private Activity(String name, int length, ArrayList<Calendar> dateRange,
					 boolean[] days, Time startTimeRange, Time endTimeRange,
					 ArrayList<TargetGroup> targetGroups, Venue venue,SiteSession session){
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
	}
	
// Builder
	
	public static class ActivityBuilder{
		// Immutable values
		private String name;						 // Name of the activity
		private int length;							 // Length of the activity (in minutes)
		private ArrayList<Calendar> dateRange;			 // Possible dates the activity can be scheduled on
		private boolean[] days;
		private Time startTimeRange;				 // Start of range of possible times the activity can be scheduled on
		private Time endTimeRange;				 	 // End of range of possible times the activity can be scheduled on
		private ArrayList<TargetGroup> targetGroups; 	 // Target groups of the activity
		private Venue venue;						 // Venue of the activity
		private SiteSession session;
		
		public ActivityBuilder(String name, int length, boolean[] days, Time startTimeRange,
							   Time endTimeRange, Venue venue,SiteSession session){
			this.name = name;
			this.length = length;
			this.days = days;
			this.startTimeRange = startTimeRange;
			this.endTimeRange = endTimeRange;
			this.venue = venue;
			this.session = session;
			dateRange = new ArrayList<Calendar>();
			targetGroups = new ArrayList<TargetGroup>();
		}
		
		
		
		public ActivityBuilder addDate(Calendar date){
			dateRange.add(date);
			return this;
		}
		
		public ActivityBuilder addTargetGroup(TargetGroup targetGroup){
			targetGroups.add(targetGroup);
			return this;
		}
		
		public Activity buildActivity(){
			if(length > (endTimeRange.getTime() - startTimeRange.getTime())/60000 &&
			   name != "" && dateRange.size() > 0 && targetGroups.size() > 0){
				return new Activity(name, length, dateRange, days, startTimeRange, endTimeRange, targetGroups, venue,session);
			}
			else{
				return null;
			}
		}
	}
	
// Getters
	
	public String getName(){
		return name;
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
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				dates.add(c);
			}
			//for each day
			for(int i = 0; i < days.length; i++) {
				//if activity can be on that day
				if( days[i] ) {
					Calendar c = Calendar.getInstance();
					c.setTime(session.getStartDate().getTime());
					int dayIndex = i + 1;
					while(c.get(Calendar.DAY_OF_WEEK) != dayIndex) {
						c.add(Calendar.DAY_OF_MONTH, 1);
					}
					while(c.compareTo(session.getEndDate()) == -1) {
						boolean found = false;
						for(Calendar x : dates) {
							if( x.compareTo(c) == 0 ) {
								found = true;
								break;
							}
						}
						if( !found ) {
							Calendar newDate = Calendar.getInstance();
							newDate.setTime(c.getTime());
							dates.add(newDate);
						}
					}
				}
			}
			allDates = dates.toArray(new Calendar[0]);
		}
		return allDates;
	}
	
	public Time getStartTimeRange(){
		return startTimeRange;
	}
	
	public Time getEndTimeRange(){
		return endTimeRange;
	}
	
	public ArrayList<TargetGroup> getTargetGroups(){
		return targetGroups;
	}
	
	public Venue getVenue(){
		return venue;
	}
	
	public Calendar getDate(){
		return date;
	}
	
	public Time getStartTime(){
		return startTime;
	}
	
	public Time getEndTime(){
		return new Time(getStartTime().getTime() + getLength() * 60000);
	}
	
// Setters
	
	public void setDate(Calendar date){
		this.date = date;
	}
	
	public void setStartTime(Time startTime){
		this.startTime = startTime;
	}
	
// Comparison
	
	public boolean hasConflictingTargetGroups(ArrayList<TargetGroup> compare){
		return !Collections.disjoint(targetGroups, compare);
	}
	
	public Activity copy() {
		ActivityBuilder ab = new ActivityBuilder(name, length, days, startTimeRange,endTimeRange,venue,session);
		for(Calendar d: dateRange) {
			ab.addDate(d);
		}
		for(TargetGroup t : targetGroups) {
			ab.addTargetGroup(t);
		}
		return ab.buildActivity();
		
	}
}
