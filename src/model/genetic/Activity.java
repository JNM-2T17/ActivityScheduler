package model.genetic;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;

public class Activity {
	// Immutable values
	private String name;						 // Name of the activity
	private int length;							 // Length of the activity (in minutes)
	private ArrayList<Date> dateRange;			 // Possible dates the activity can be scheduled on
	private Time startTimeRange;				 // Start of range of possible times the activity can be scheduled on
	private Time endTimeRange;				 	 // End of range of possible times the activity can be scheduled on
	private ArrayList<String> targetGroups; 	 // Target groups of the activity
	// NOTE: Change this to ArrayList of target groups
	private String venue;						 // Venue of the activity
	
	// Mutable values
	private Date date;
	private Time startTime;
	
// Constructor
	
	private Activity(String name, int length, ArrayList<Date> dateRange,
					 Time startTimeRange, Time endTimeRange,
					 ArrayList<String> targetGroups, String venue){ // NOTE: Change this to ArrayList of target groups
		this.name = name;
		this.length = length;
		this.dateRange = dateRange;
		this.startTimeRange = startTimeRange;
		this.endTimeRange = endTimeRange;
		this.targetGroups = targetGroups;
		this.venue = venue;
	}
	
// Builder
	
	private class ActivityBuilder{
		// Immutable values
		private String name;						 // Name of the activity
		private int length;							 // Length of the activity (in minutes)
		private ArrayList<Date> dateRange;			 // Possible dates the activity can be scheduled on
		private Time startTimeRange;				 // Start of range of possible times the activity can be scheduled on
		private Time endTimeRange;				 	 // End of range of possible times the activity can be scheduled on
		private ArrayList<String> targetGroups; 	 // Target groups of the activity
		// NOTE: Change this to ArrayList of target groups
		private String venue;						 // Venue of the activity
		
		public ActivityBuilder(String name, int length, Time startTimeRange,
							   Time endTimeRange, String venue){
			this.name = name;
			this.length = length;
			this.startTimeRange = startTimeRange;
			this.endTimeRange = endTimeRange;
			this.venue = venue;
			
			dateRange = new ArrayList<Date>();
			targetGroups = new ArrayList<String>(); // NOTE: Change this to ArrayList of target groups
		}
		
		public void addDate(Date date){
			dateRange.add(date);
		}
		
		public void addTargetGroup(String targetGroup){ // NOTE: Change this to ArrayList of target groups
			targetGroups.add(targetGroup);
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
	
	public ArrayList<Date> getDateRange(){
		return dateRange;
	}
	
	public Time getStartTimeRange(){
		return startTimeRange;
	}
	
	public Time getEndTimeRange(){
		return endTimeRange;
	}
	
	public ArrayList<String> getTargetGroups(){ // NOTE: Change this to ArrayList of target groups
		return targetGroups;
	}
	
	public String getVenue(){
		return venue;
	}
	
	public Date getDate(){
		return date;
	}
	
	public Time getStartTime(){
		return startTime;
	}
	
// Setters
	
	public void setDate(Date date){
		this.date = date;
	}
	
	public void setStartTime(Time startTime){
		this.startTime = startTime;
	}
	
}
