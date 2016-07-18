package model.genetic;

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
		return null;
	}
}
