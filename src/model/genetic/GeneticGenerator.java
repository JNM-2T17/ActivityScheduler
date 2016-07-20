package model.genetic;

public abstract class GeneticGenerator {
	private int populationSize;
	private double fitnessThreshold;
	private double elitismRate; 
	private double mutationRate;
	private int maxIter;
	private double changeThreshold;
	private boolean changedPop;
	private double totalFitness;
	protected Chromosome[] population;
	
	public GeneticGenerator(int populationSize, double fitnessThreshold,
			double elitismRate, double mutationRate,int maxIter,int changeThreshold) {
		super();
		this.populationSize = populationSize;
		this.fitnessThreshold = fitnessThreshold;
		this.elitismRate = elitismRate;
		this.mutationRate = mutationRate;
		this.maxIter = maxIter;
		this.changeThreshold = changeThreshold;
		changedPop = false;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public double getFitnessThreshold() {
		return fitnessThreshold;
	}

	public void setFitnessThreshold(double fitnessThreshold) {
		this.fitnessThreshold = fitnessThreshold;
	}

	public double getElitismRate() {
		return elitismRate;
	}

	public void setElitismRate(double elitismRate) {
		this.elitismRate = elitismRate;
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}
	
	public int getMaxIter() {
		return maxIter;
	}

	public void setMaxIter(int maxIter) {
		this.maxIter = maxIter;
	}

	private void setPopulation(Chromosome[] population) {
		this.population = population;
		changedPop = true;
	}

	private void initializePopulation() {
		Chromosome[] pop = new Chromosome[populationSize];
		for(int i = 0; i < populationSize; i++) {
			pop[i] = generateRandomChromosome();
		}
		setPopulation(pop);
	}
	
	protected abstract Chromosome generateRandomChromosome();
	
	public double getTotalFitness() {
		if( changedPop ) {
			totalFitness = 0;
			for(Chromosome c: population) {
				totalFitness += c.getFitness();
			}
		} 
		return totalFitness;
	}
	
	public Chromosome generate() {
		initializePopulation();
		changedPop = true;
		Chromosome best = population[0];
		for(int i = 1; i < population.length; i++) {
			if( population[i].getFitness() > best.getFitness() ) {
				best = population[i];
			}
		}
		int iter = 0;
		int elite = (int)(populationSize * elitismRate);
		int mutate = (int)(populationSize * mutationRate);
		double prevTotalFitness = getTotalFitness() - 2 * changeThreshold;
		double currTotalFitness = getTotalFitness();
		
		//while best is below the fitness threshold and less than max iterations
		//and change in fitness is above change threshold
		while(fitnessThreshold - best.getFitness() > 0.00001 && iter < maxIter && 
				Math.abs(currTotalFitness - prevTotalFitness) - changeThreshold > 0.00001) {
			prevTotalFitness = currTotalFitness;
			Chromosome[] newPop = new Chromosome[populationSize];
			int currIndex = 0;
			
			//get elite members
			for(int i = 0; i < elite; i++,currIndex++) {
				newPop[currIndex] = population[getRandomChromosomeIndex()].copy();
			}
			
			//breed children
			while(currIndex < populationSize) {
				Chromosome c1 = population[getRandomChromosomeIndex()];
				Chromosome c2 = population[getRandomChromosomeIndex()];
				Chromosome[] children = c1.crossover(c2);
				newPop[currIndex] = children[0];
				currIndex++;
				if( currIndex < populationSize) {
					newPop[currIndex] = children[1];
					currIndex++;
				}
			}
			
			//replace population
			setPopulation(newPop);
			
			//mutate children
			for(int i = 0; i < mutate; i++) {
				population[getRandomChromosomeIndex()].mutate();
			}
			
			currTotalFitness = getTotalFitness();
			
			//get best
			for(int i = 0; i < population.length; i++) {
				if( population[i].getFitness() > best.getFitness() ) {
					best = population[i];
				}
			}
			iter++;
			System.out.println("ITERATION " + iter + 
								"\nMax Iterations:\t" + maxIter +
								"\nBest Fitness:\t" + best.getFitness() + 
								"\nFitness Threshold:\t" + fitnessThreshold +
								"\nChange in Fitness:\t" + Math.abs(currTotalFitness - prevTotalFitness) + 
								"\nChange Threshold:\t" + changeThreshold + "\n\n");
		}
		
		return best;
	}
	
	public int getRandomChromosomeIndex() {
		double random = Math.random() * getTotalFitness();
		
		int i = 0;
		while(random > 0.00001) {
			random -= population[i].getFitness();
			i++;
		}
		return i - 1;
	}
}
