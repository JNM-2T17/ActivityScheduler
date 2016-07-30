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
	private Time startTimeRange;				 // Start of range of possible times the activity can be scheduled on
	private Time endTimeRange;				 	 // End of range of possible times the activity can be scheduled on
	private ArrayList<TargetGroup> targetGroups; 	 // Target groups of the activity
	private Venue venue;						 // Venue of the activity
	
	// Mutable values
	private Calendar date;
	private Time startTime;
	
// Constructor
	
	private Activity(String name, int length, ArrayList<Calendar> dateRange,
					 Time startTimeRange, Time endTimeRange,
					 ArrayList<TargetGroup> targetGroups, Venue venue){
		this.name = name;
		this.length = length;
		this.dateRange = dateRange;
		this.startTimeRange = startTimeRange;
		this.endTimeRange = endTimeRange;
		this.targetGroups = targetGroups;
		this.venue = venue;
	}
	
// Builder
	
	public static class ActivityBuilder{
		// Immutable values
		private String name;						 // Name of the activity
		private int length;							 // Length of the activity (in minutes)
		private ArrayList<Calendar> dateRange;			 // Possible dates the activity can be scheduled on
		private Time startTimeRange;				 // Start of range of possible times the activity can be scheduled on
		private Time endTimeRange;				 	 // End of range of possible times the activity can be scheduled on
		private ArrayList<TargetGroup> targetGroups; 	 // Target groups of the activity
		private Venue venue;						 // Venue of the activity
		
		public ActivityBuilder(String name, int length, Time startTimeRange,
							   Time endTimeRange, Venue venue){
			this.name = name;
			this.length = length;
			this.startTimeRange = startTimeRange;
			this.endTimeRange = endTimeRange;
			this.venue = venue;
			
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
				return new Activity(name, length, dateRange, startTimeRange, endTimeRange, targetGroups, venue);
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
	
	public ArrayList<Calendar> getDateRange(){
		return dateRange;
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
		ActivityBuilder ab = new ActivityBuilder(name, length, startTimeRange,endTimeRange,venue);
		for(Calendar d: dateRange) {
			ab.addDate(d);
		}
		for(TargetGroup t : targetGroups) {
			ab.addTargetGroup(t);
		}
		return ab.buildActivity();
		
	}
}
