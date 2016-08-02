package model.genetic;

import java.util.ArrayList;

public abstract class GeneticGenerator {
	private int populationSize;
	private double fitnessThreshold;
	private double elitismRate; 
	private double mutationRate;
	private int maxIter;
	private boolean changedPop;
	private double totalFitness;
	protected Chromosome[] population;
	
	public GeneticGenerator(int populationSize, double fitnessThreshold,
			double elitismRate, double mutationRate,int maxIter) {
		this.populationSize = populationSize;
		this.fitnessThreshold = fitnessThreshold;
		this.elitismRate = elitismRate;
		this.mutationRate = mutationRate;
		this.maxIter = maxIter;
		changedPop = false;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public double getFitnessThreshold() {
		return fitnessThreshold;
	}

	public double getElitismRate() {
		return elitismRate;
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public int getMaxIter() {
		return maxIter;
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
			changedPop = false;
		} 
		return totalFitness;
	}
	
	public Chromosome generate() {
		initializePopulation();
		Chromosome best = population[0];
		for(int i = 1; i < population.length; i++) {
			if( population[i].getFitness() > best.getFitness() ) {
				best = population[i];
			}
		}
		int iter = 0;
		int elite = (int)(populationSize * elitismRate);
		int mutate = (int)(populationSize * mutationRate);
		
		System.out.println("Best:\t" + best + 
				"\nMax Iterations:\t" + maxIter +
				"\nBest Fitness:\t" + best.getFitness() + 
				"\nFitness Threshold:\t" + fitnessThreshold + 
				"\nTotal Fitness:\t" + getTotalFitness() + "\n\n");
		
		//while best is below the fitness threshold and less than max iterations
		//and change in fitness is above change threshold
		while(fitnessThreshold - best.getFitness() > 0.00001 && iter < maxIter) {
//			for(Chromosome c : population) {
//				System.out.println(c);
//			}
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
			int[] mutates = getRandomUniformIndices(mutate);
			for(int i : mutates) {
				population[i].mutate();
			}
			
			//get best
			for(int i = 0; i < population.length; i++) {
				if( population[i].getFitness() > best.getFitness() ) {
					best = population[i];
				}
			}
			iter++;
			System.out.println("ITERATION " + iter + 
								"\nBest:\t" + best + 
								"\nMax Iterations:\t" + maxIter +
								"\nBest Fitness:\t" + best.getFitness() + 
								"\nFitness Threshold:\t" + fitnessThreshold + 
								"\nTotal Fitness:\t" + getTotalFitness() + "\n\n");
		}
		
		return best;
	}
	
	public int[] getRandomUniformIndices(int n) {
		ArrayList<Integer> nums = new ArrayList<Integer>();
		for(int i = 0; i < populationSize; i++) {
			nums.add(i);
		}
		int[] indices = new int[n];
		for(int i = 0; i < n; i++) {
			int index = (int)(Math.random() * nums.size());
			indices[i] = nums.get(index);
			nums.remove(index);
		}
		return indices;
	}
	
	public int getRandomChromosomeIndex() {
		double random = Math.random() * getTotalFitness();
		
		int i = 0;
		while(i < getPopulationSize() && random > -0.00001) {
			random -= population[i].getFitness();
			i++;
		}
		return i - 1;
	}
}
