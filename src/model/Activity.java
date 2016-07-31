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
	private Calendar startTimeRange;				 // Start of range of possible times the activity can be scheduled on
	private Calendar endTimeRange;				 	 // End of range of possible times the activity can be scheduled on
	private ArrayList<TargetGroup> targetGroups; 	 // Target groups of the activity
	private Venue venue;						 // Venue of the activity
	private SiteSession session;
	private Calendar[] allDates;
	
	// Mutable values
	private Calendar startTime;
	
// Constructor
	
	private Activity(String name, int length, ArrayList<Calendar> dateRange,
					 boolean[] days, Calendar startTimeRange, Calendar endTimeRange,
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
		private Calendar startTimeRange;				 // Start of range of possible times the activity can be scheduled on
		private Calendar endTimeRange;				 	 // End of range of possible times the activity can be scheduled on
		private ArrayList<TargetGroup> targetGroups; 	 // Target groups of the activity
		private Venue venue;						 // Venue of the activity
		private Calendar startTime;
		private SiteSession session;
		
		public ActivityBuilder(String name, int length, boolean[] days, Calendar startTimeRange,
							   Calendar endTimeRange, Venue venue,SiteSession session){
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
		
		public ActivityBuilder setStartTime(Calendar startDate) {
			this.startTime = startTime;
			
			return this;
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
			if(length <= (endTimeRange.getTime().getTime() - startTimeRange.getTime().getTime())/60000 &&
			   name != "" && dateRange.size() > 0 && targetGroups.size() > 0){
				Activity a = new Activity(name, length, dateRange, days, startTimeRange, endTimeRange, targetGroups, venue,session);
				a.setStartTime(startTime);
				return a;
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
		ActivityBuilder ab = new ActivityBuilder(name, length, days, startTimeRange,endTimeRange,venue,session);
		for(Calendar d: dateRange) {
			ab.addDate(d);
		}
		for(TargetGroup t : targetGroups) {
			ab.addTargetGroup(t);
		}
		return ab.setStartTime(startTime).buildActivity();
		
	}
}
