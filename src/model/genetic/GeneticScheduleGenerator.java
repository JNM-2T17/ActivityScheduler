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
	
	public static void main(String[] args) {
		SiteSession ss = new SiteSession(1,1,"Sample","1,0,0,0,0,0,0",CalendarFactory.createCalendar(2016, 7, 1),CalendarFactory.createCalendar(2016, 7, 15));
		ss.addBlackTime(CalendarFactory.createCalendarTime(7, 30, 00), CalendarFactory.createCalendarTime(8, 30, 00));
		ss.addBlackTime(CalendarFactory.createCalendarTime(13, 00, 00), CalendarFactory.createCalendarTime(16, 00, 00));
		ss.addBlackdate(CalendarFactory.createCalendar(2016, 7, 8));
//		for(TimeRange tr: ss.getBlacktimes()) {
//			System.out.println(tr);
//		}
		ArrayList<Activity> acts = new ArrayList<Activity>();
		TargetGroup[] targetGroup = new TargetGroup[] {
				new TargetGroup(1,"ST"),
				new TargetGroup(2,"NE"),
				new TargetGroup(3,"CSE"),
				new TargetGroup(4,"IST")
		};
		Builder ab = new Activity.Builder(1,"Act 1",240,"0,0,0,1,0,0,0",
				CalendarFactory.createCalendarTime(8,0,0),
				CalendarFactory.createCalendarTime(21,0,0),new Venue(1,"ISR"),ss);
		ab.addTargetGroup(targetGroup[0]);
		ab.addTargetGroup(targetGroup[1]);
		ab.addTargetGroup(targetGroup[2]);
		acts.add(ab.buildActivity());
		ab = new Activity.Builder(2,"Act 2",240,"0,0,0,1,0,0,0",
				CalendarFactory.createCalendarTime(12,0,0),
				CalendarFactory.createCalendarTime(21,0,0),new Venue(1,"ISR"),ss);
		ab.addTargetGroup(targetGroup[0]);
		ab.addTargetGroup(targetGroup[2]);
		acts.add(ab.buildActivity());
		ab = new Activity.Builder(3,"Act 3",300,"0,0,0,1,0,0,0",
				CalendarFactory.createCalendarTime(12,0,0),
				CalendarFactory.createCalendarTime(21,0,0),new Venue(2,"Gox Lobby"),ss);
		ab.addTargetGroup(targetGroup[2]);
		ab.addTargetGroup(targetGroup[3]);
//		ab.addDate(CalendarFactory.createCalendar(2017,11, 25));
		acts.add(ab.buildActivity());
		System.out.println(acts);
		ab = new Activity.Builder(4,"Act 4",30,"0,0,0,1,0,0,0",
				CalendarFactory.createCalendarTime(12,30,0),
				CalendarFactory.createCalendarTime(16,0,0),new Venue(2,"Gox Lobby"),ss);
		ab.addTargetGroup(targetGroup[2]);
		ab.addTargetGroup(targetGroup[3]);
		acts.add(ab.buildActivity());
		System.out.println(acts);
		GeneticScheduleGenerator gsg = new GeneticScheduleGenerator(50, 0.3, 0.2, 0.4, 200000, acts.toArray(new Activity[0]));
		Chromosome sc = gsg.generate();
		System.out.println(sc);
	}
	
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
