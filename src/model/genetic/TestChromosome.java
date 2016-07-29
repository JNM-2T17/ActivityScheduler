package model.genetic;

public class TestChromosome implements Chromosome {
	public static final int BIT_LENGTH = 57;
	private byte[] bits; 
	
	public TestChromosome() {
		bits = new byte[BIT_LENGTH];
	}
	
	public void set(int index) {
		if(index >= 0 && index < bits.length) {
			bits[index] = 1;
		}
	}
	
	@Override
	public double getFitness() {
		// TODO Auto-generated method stub
		int fitness = 0;
		for(int i = 0; i < bits.length; i++) {
			fitness += bits[i];
		}
		return fitness * fitness;
	}

	@Override
	public Chromosome[] crossover(Chromosome c) {
		// TODO Auto-generated method stub
		TestChromosome tc = (TestChromosome)c;
		TestChromosome[] children = new TestChromosome[]{
				new TestChromosome(),
				new TestChromosome()
		};
		int split = (int)(Math.random() * bits.length);
		
		for(int i = 0; i < bits.length; i++) {
			if( i < split ) {
				children[0].bits[i] = bits[i];
				children[1].bits[i] = tc.bits[i];
			} else {
				children[1].bits[i] = bits[i];
				children[0].bits[i] = tc.bits[i];
			}
		}
		return children;
	}

	@Override
	public void mutate() {
		// TODO Auto-generated method stub
		for(int i = 0; i < bits.length; i++) {
			bits[i] = (byte) (1 - bits[i]);
		}
	}

	@Override
	public Chromosome copy() {
		// TODO Auto-generated method stub
		TestChromosome tc = new TestChromosome();
		for(int i = 0; i < bits.length; i++) {
			tc.bits[i] = bits[i];
		}
		return tc;
	}

	public String toString() {
		String s = "";
		for(int i =0 ; i < bits.length; i++) {
			s += bits[i];
		}
		return s;
	}
}
