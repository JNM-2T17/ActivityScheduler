package model.genetic;

import java.util.Random;

public class GeneticScheduleGenerator extends GeneticGenerator {

	public GeneticScheduleGenerator(int populationSize,
			double fitnessThreshold, double elitismRate, double mutationRate,
			int maxIter, int changeThreshold) {
		super(populationSize, fitnessThreshold, elitismRate, mutationRate, maxIter,
				changeThreshold);
	}

	@Override
	protected Chromosome generateRandomChromosome() {
		// TODO Randomize One Chromosome here
		
		// for every activity

			// set date as random date from the date range
				// Random rand = new Random();
				// int randIndex = rand.nextInt(activity.getDateRange().size() - 1);
			
			// set time as random time from start of time range -> end of time range - activity length
		
		return null;
	}
}
