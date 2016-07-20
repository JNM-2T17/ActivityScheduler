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
		// For every pair of activities (do not repeat pairs)
			// If conflicting times
			// if (start time 1 >= start time 2 && start time 1 < end time 2) || (end time 1 > start time 2 && end time 1 <= end time 2) 
			
				// If conflicting venues
			
				// Else if conflicting target groups
				// if(!Collections.disjoint(target groups 1, target groups 2))
		
				// Else
			
			// Else if same date && same target group
		
		// TODO Auto-generated method stub
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
