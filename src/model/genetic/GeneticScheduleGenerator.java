package model.genetic;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;

import model.Activity;
import model.Activity.Builder;
import model.CalendarFactory;
import model.SiteSession;
import model.TargetGroup;
import model.TimeRange;
import model.Venue;

public class GeneticScheduleGenerator extends GeneticGenerator {
	private Activity[] activities;
	
	public GeneticScheduleGenerator(int populationSize,
			double fitnessThreshold, double elitismRate, double mutationRate,
			int maxIter,Activity[] activities) {
		super(populationSize, fitnessThreshold, elitismRate, mutationRate, maxIter);
		this.activities = activities;
	}

	@Override
	protected Chromosome generateRandomChromosome() {
		// TODO Randomize One Chromosome here
		ScheduleChromosome sc = new ScheduleChromosome(activities);
		sc.randomize();
			
		return sc;
	}
}
