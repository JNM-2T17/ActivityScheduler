package model.genetic;

public interface Chromosome {
	public double getFitness();
	public Chromosome[] crossover(Chromosome c);
	public void mutate();
	public Chromosome copy();
}
