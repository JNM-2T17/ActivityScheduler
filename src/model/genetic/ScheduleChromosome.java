package model.genetic;

import java.util.ArrayList;
import java.util.Calendar;

import model.Activity;

public class ScheduleChromosome implements Chromosome {
	
	private ArrayList<Activity> activities;
	
	public ScheduleChromosome() {
		activities = new ArrayList<Activity>();
	}
	
	public ScheduleChromosome(ArrayList<Activity> activities) {
		this.activities = new ArrayList<Activity>();
		for(Activity a : activities) {
			this.activities.add(a.copy());
		}
	}
	
	public void addActivity(Activity a) {
		activities.add(a);
	}
	
	public Activity getActivity(int index) {
		return activities.get(index);
	}
	
	public void randomize() {
		for(Activity a : activities) {
			a.randomizeTime();
		}
	}
	
	@Override
	public double getFitness() {
		int activityCount = activities.size();
		double fitness = 0;
		
		for(int i = 0; i < activityCount - 1; i++){
			for(int j = i + 1; j < activityCount; j++){
				Activity activity1 = activities.get(i);
				Activity activity2 = activities.get(j);
				
				// If conflicting times
				// if (start time 1 >= start time 2 && start time 1 < end time 2) || (end time 1 > start time 2 && end time 1 <= end time 2)
				if(activity1.getStartTime().compareTo(activity2.getStartTime()) >= 0 &&
				   activity1.getStartTime().compareTo(activity2.getEndTime()) < 0 ||
				   activity1.getEndTime().compareTo(activity2.getStartTime()) > 0 &&
				   activity1.getEndTime().compareTo(activity2.getEndTime()) <= 0){
				 
					// If conflicting venues
					if(activity1.getVenue().getId() == activity2.getVenue().getId()){
						fitness += 100;
					}
				
					// Else if conflicting target groups
					else if(activity1.hasConflictingTargetGroups(activity2)){
						fitness += 70;
					}
			
					// Else
					else{
						fitness += 20;
					}
				}
				
				// Else if same date && same target group
				else if(activity1.getStartTime().get(Calendar.YEAR) == 
						activity2.getStartTime().get(Calendar.YEAR) &&
						activity1.getStartTime().get(Calendar.MONTH) == 
						activity2.getStartTime().get(Calendar.MONTH) &&
						activity1.getStartTime().get(Calendar.DAY_OF_MONTH) == 
						activity2.getStartTime().get(Calendar.DAY_OF_MONTH) &&
						activity1.hasConflictingTargetGroups(activity2)){
					fitness += 30;
				}
			}
		}
		
		return 1.0 / ( fitness + 1 );
	}

	@Override
	public Chromosome[] crossover(Chromosome c) {
		ScheduleChromosome sc = (ScheduleChromosome)c;
		ScheduleChromosome[] children = new ScheduleChromosome[]{
				new ScheduleChromosome(),
				new ScheduleChromosome()
		};
		int crossPoint = (int)(Math.random() * activities.size());
		for(int i = 0; i < activities.size(); i++) {
			Activity left = getActivity(i).copy();
			Activity right = sc.getActivity(i).copy();
			if( i < crossPoint ) {
				children[0].addActivity(left);
				children[1].addActivity(right);
			} else {
				children[1].addActivity(left);
				children[0].addActivity(right);
			}
		}
		return children;
	}

	@Override
	public void mutate() {
		// TODO Auto-generated method stub
		for(Activity a : activities) {
			if( Math.random() <= 0.7 ) {
				a.randomizeTime();
			}
		}
	}

	@Override
	public Chromosome copy() {
		// TODO Auto-generated method stub
		ScheduleChromosome sc = new ScheduleChromosome();
		for(Activity a: activities) {
			sc.addActivity(a.copy());
		}
		return sc;
	}

	public String toString() {
		String ret = "Conflict Points:\t" + ((int)(1 / getFitness()) - 1);
		for(Activity a : activities) {
			ret += "\n\t" + a.toString();
		}
		return ret;
	}
}
