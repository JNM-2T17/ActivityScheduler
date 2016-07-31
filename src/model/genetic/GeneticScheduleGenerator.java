package model.genetic;

import java.sql.Date;
import java.sql.Time;
import java.util.Random;

import model.Activity;
import model.Activity.ActivityBuilder;
import model.TargetGroup;

public class GeneticScheduleGenerator extends GeneticGenerator {

	public GeneticScheduleGenerator(int populationSize,
			double fitnessThreshold, double elitismRate, double mutationRate,
			int maxIter) {
		super(populationSize, fitnessThreshold, elitismRate, mutationRate, maxIter);
	}

	@Override
	protected Chromosome generateRandomChromosome() {
		// TODO Randomize One Chromosome here
		
		// for every activity
		// Temp
//		ActivityBuilder builder = new Activity.ActivityBuilder("Activity Name", 180, new Time(9, 0, 0), new Time(18, 0, 0), "G301");
//		builder.addDate(new Date(2016, 7, 1));
//		builder.addDate(new Date(2016, 7, 8));
//		builder.addDate(new Date(2016, 7, 15));
//		builder.addDate(new Date(2016, 7, 22));
//		builder.addDate(new Date(2016, 7, 29));
//		builder.addTargetGroup(new TargetGroup("1st Year CS-ST"));
//		Activity activity = builder.buildActivity();
			
		// set date as random date from the date range
//		activity.getAllDates().length
//		int randIndex = rand.nextInt(activity.getAllDates().length);
//		activity.setDate(activity.getDateRange().get(randIndex));
		
		// set time as random time from start of time range -> end of time range - activity length
//		long randTime = rand.nextLong() % (activity.getEndTimeRange().getTime() - activity.getLength() * 60000)/60000/15;
//		Time startTime = new Time(randTime * 15 * 60000);
//		activity.setStartTime(startTime);
			
		return null;
	}
}
