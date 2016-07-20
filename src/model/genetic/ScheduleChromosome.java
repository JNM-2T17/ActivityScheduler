package model.genetic;

import java.util.ArrayList;

=======
>>>>>>> origin/master
=======
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
