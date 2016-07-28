package model.genetic;

public class TestGenerator extends GeneticGenerator {

	public static void main(String[] args) {
		TestGenerator tg = new TestGenerator(50,Math.pow(TestChromosome.BIT_LENGTH,2),0.2,0.4,1000000);
		System.out.println(tg.generate());
	}
	
	public TestGenerator(int populationSize, double fitnessThreshold,
			double elitismRate, double mutationRate, int maxIter) {
		super(populationSize, fitnessThreshold, elitismRate, mutationRate, maxIter);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Chromosome generateRandomChromosome() {
		// TODO Auto-generated method stub
		TestChromosome tx = new TestChromosome();
		for(int i = 0; i < TestChromosome.BIT_LENGTH; i++) {
			if( (int)(Math.random() * 2) == 1 ) {
				tx.set(i);
			}
		}
		return tx;
	}

}
