package model.genetic;

import java.util.ArrayList;

public class ScheduleChromosome implements Chromosome {
	
	private ArrayList<Activity> activities;
	
	public ScheduleChromosome() {
		activities = new ArrayList<Activity>();
	}
	
	public void addActivity(Activity a) {
		activities.add(a);
	}
	
	public Activity getActivity(int index) {
		return activities.get(index);
	}
	
	@Override
	public double getFitness() {
		int activityCount = activities.size();
		double fitness = 0;
		
		for(int i = 0; i < activityCount - 1; i++){
			for(int j = 0; j < activityCount - i; j++){
				Activity activity1 = activities.get(i);
				Activity activity2 = activities.get(j);
				
				// If conflicting times
				// if (start time 1 >= start time 2 && start time 1 < end time 2) || (end time 1 > start time 2 && end time 1 <= end time 2)
				if(activity1.getStartTime().compareTo(activity2.getStartTime()) >= 0 &&
				   activity1.getStartTime().compareTo(activity2.getEndTime()) < 0 ||
				   activity1.getEndTime().compareTo(activity2.getStartTime()) > 0 &&
				   activity1.getEndTime().compareTo(activity2.getEndTime()) <= 0){
				 
					// If conflicting venues
					if(activity1.getVenue().equals(activity2.getVenue())){
						fitness += 100;
					}
				
					// Else if conflicting target groups
					else if(activity1.hasConflictingTargetGroups(activity2.getTargetGroups())){
						fitness += 70;
					}
			
					// Else
					else{
						fitness += 20;
					}
				}
				
				// Else if same date && same target group
				else if(activity1.getDate().compareTo(activity2.getDate()) == 0 &&
						activity1.hasConflictingTargetGroups(activity2.getTargetGroups())){
					fitness += 30;
				}
			}
		}
		
		return 0;
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
			if( i < crossPoint ) {
				children[0].addActivity(getActivity(i).copy());
				children[1].addActivity(sc.getActivity(i).copy());
			} else {
				children[1].addActivity(getActivity(i).copy());
				children[0].addActivity(sc.getActivity(i).copy());
			}
		}
		return children;
	}

	@Override
	public void mutate() {
		// TODO Auto-generated method stub
		
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

}
