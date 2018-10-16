package algorithm;

import configuration.ConfigParams;
import configuration.Configuration;
import individuals.GeneTypes;
import individuals.GenoRepresentation;
import individuals.Individual;
import individuals.Population;
import initialization.RandomGenoInitializer;
import jdk.nashorn.internal.ir.annotations.Ignore;
import mutation.GaussianMutation;
import mutation.Mutation;
import selection.ParentSelection;
import recombination.RandomRecombination;
import recombination.Recombination;
import selection.SurvivorSelection;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by phlippe on 06.09.18.
 */
public class EvolutionaryCycle implements EvolutionaryAlgorithm
{

	private Population population;
	private GenoRepresentation genoRepresentation;
	private SurvivorSelection survivorSelection;
	private ParentSelection parentSelection;
	private Recombination recombination;
	private ArrayList<Mutation> mutations;
	private Configuration myConfig;
	private int number_cycles;
	private Tracer tracer;

	public EvolutionaryCycle(Configuration myConfig){
		this.myConfig = myConfig;
		population = new Population(myConfig.getPopulationSize());
		genoRepresentation = myConfig.getRepresentation();
		survivorSelection = myConfig.getSurvivorSelection();
		parentSelection = myConfig.getParentSelection();
		recombination = myConfig.getRecombination();
		mutations = myConfig.getMutationOperators();
		number_cycles = 0;
		myConfig.setEvolutionaryAlgorithm(this);
		tracer = new Tracer(false, "");
	}

	@Override
	public void initialize(){
		population.initialize(genoRepresentation, myConfig.getGenoInitializer(), myConfig.getAddParamsInitializer());
		for(int i=0;i<population.size();i++){
			if(TheOptimizers.evaluation_ != null)
			{
				double fitness = (double) TheOptimizers.evaluation_.evaluate(population.get(i).getPhenotype());
				population.get(i).setFitness(fitness);
			}
			else{
				population.get(i).setFitness(-1);
			}
		}
		population.reevaluateMaxFitness();
		population.increaseAge();
		population.setConfigParams(myConfig.getParameters());
		tracer.initialize();
		tracer.addTraceFile(TraceTags.MAX_FITNESS);
		tracer.addTraceFile(TraceTags.MEAN_FITNESS);
		tracer.addTraceFile(TraceTags.MIN_FITNESS);
		tracer.addTraceFile(TraceTags.MEAN_DISTANCE);
		tracer.addTraceFile(TraceTags.MEAN_POSITION);
		tracer.addTraceFile(TraceTags.MEAN_MULTI_SIGMA);
		tracer.addTraceFile(TraceTags.MEAN_FITNESS_FACTOR);
		tracer.addTraceFile(TraceTags.FITNESS_SHARING_BETA);
		tracer.addTraceFile(TraceTags.FITNESS_SHARING_DISTANCE_SUM);
		tracer.addTraceFile(TraceTags.MEAN_AGE_DEAD);
		tracer.addTraceFile(TraceTags.MEAN_AGE_POPULATION);
		tracer.addTraceFile(TraceTags.MEAN_DEAD_OFFSPRINGS);
		tracer.addTraceFile(TraceTags.MEAN_FITNESS_DEAD);
		tracer.addTraceFile(TraceTags.MEAN_FITNESS_FACTOR_DEAD);
		tracer.addTraceFile(TraceTags.MIN_AGE_DEAD);
		tracer.addTraceFile(TraceTags.MEAN_SHARED_FITNESS);
		tracer.addTraceFile(TraceTags.DESIRED_MEAN_DISTANCE);
		tracer.addTraceFile(TraceTags.DESIRED_MEAN_FACTOR);
		tracer.addTraceFile(TraceTags.POPULATION_POSITION);
		tracer.addTraceFile(TraceTags.POPULATION_DIST);
	}

	@Override
	public void run_single_cycle(){
		population.prepareCycle();
		// 1. Parent selection
		int[][] selected_parents = parentSelection.selectParent(population, myConfig.getNumberOfRecombinations(), myConfig.getParentArity());
		// 2. recombination.Recombination / crossover
		ArrayList<Individual> children = recombination.recombineAll(population, selected_parents);
		// 3. mutation.Mutation
		for(Mutation mut: mutations){
			for(Individual new_ind: children){
				mut.mutate(new_ind);
			}
		}
		// 4. Fitness evaluation
		for(Individual child:children){
			double fitness = (double) TheOptimizers.evaluation_.evaluate(child.getPhenotype());
			child.setFitness(fitness);
		}
		population.interactWithNewChildren(children);
		// 5. Survivor selection
		ArrayList<Individual> removed_individuals = survivorSelection.selectSurvivors(population, children);
		population.increaseAge();
		number_cycles++;
		if(tracer.isActive()){
			traceCyclePopulation();
		}
		population.endCycle();
		if(tracer.isActive()) {
			tracePopulation();
			traceRemovedIndividuals(removed_individuals);
		}
	}

	private void traceCyclePopulation(){
		tracer.addTraceContent(TraceTags.MEAN_FITNESS_FACTOR, population.getMeanFitnessFactor());
		tracer.addTraceContent(TraceTags.MEAN_SHARED_FITNESS, population.getMeanSharedFitness());
		if(number_cycles % 100 == 0) {
			tracer.addTraceContent(TraceTags.POPULATION_POSITION, population.traceIndDist());
			tracer.addTraceContent(TraceTags.POPULATION_DIST, population.traceDistToCenter());
		}
	}

	private void traceRemovedIndividuals(ArrayList<Individual> removed_individuals) {
		double mean_age = 0.0;
		double killed_offsprings = 0.0;
		double mean_fitness = 0.0;
		double mean_fitness_factor = 0.0;
		double min_age = Double.MAX_VALUE;
		for(Individual i: removed_individuals){
			mean_age += i.getAge();
			killed_offsprings += (i.getAge() == 0 ? 1 : 0);
			mean_fitness += i.getPureFitness();
			mean_fitness_factor += i.getFitnessFactor();
			if(i.getAge() < min_age)
				min_age = i.getAge();
		}
		mean_age /= removed_individuals.size();
		killed_offsprings /= removed_individuals.size();
		mean_fitness /= removed_individuals.size();
		mean_fitness_factor /= removed_individuals.size();
		tracer.addTraceContent(TraceTags.MEAN_AGE_DEAD, mean_age);
		tracer.addTraceContent(TraceTags.MEAN_FITNESS_DEAD, mean_fitness);
		tracer.addTraceContent(TraceTags.MEAN_DEAD_OFFSPRINGS, killed_offsprings);
		tracer.addTraceContent(TraceTags.MEAN_FITNESS_FACTOR_DEAD, mean_fitness_factor);
		tracer.addTraceContent(TraceTags.MIN_AGE_DEAD, min_age);
	}

	private void tracePopulation(){
		tracer.addTraceContent(TraceTags.MAX_FITNESS, population.getMaxIndividual().getPureFitness());
		tracer.addTraceContent(TraceTags.MIN_FITNESS, population.getMinIndividual().getPureFitness());
		tracer.addTraceContent(TraceTags.MEAN_FITNESS, population.getMeanFitness());
		tracer.addTraceContent(TraceTags.MEAN_POSITION, population.getMeanPosition());
		tracer.addTraceContent(TraceTags.MEAN_DISTANCE, population.getMeanDistance(population.getMeanPosition()));
		tracer.addTraceContent(TraceTags.MEAN_MULTI_SIGMA, population.getOverallMeanMultiSigma());
		tracer.addTraceContent(TraceTags.FITNESS_SHARING_BETA, population.getFitnessSharingBeta());
		tracer.addTraceContent(TraceTags.FITNESS_SHARING_DISTANCE_SUM, population.getFitnessSharingMeanDistanceSum());
		tracer.addTraceContent(TraceTags.MEAN_AGE_POPULATION, population.getMeanAge());
		tracer.addTraceContent(TraceTags.DESIRED_MEAN_DISTANCE, population.getDesiredMeanDistance());
		tracer.addTraceContent(TraceTags.DESIRED_MEAN_FACTOR, population.getDesiredMeanFactor());
	}

	@Override
	public Population getPopulation(){
		return population;
	}

	public Configuration getMyConfig(){
		return myConfig;
	}

	@Override
	public int getEvalsPerCycle(){
	    return myConfig.getNumberOfRecombinations() * myConfig.getParentArity();
    }

    @Override
    public String getName(){
	    return myConfig.getName();
    }

	@Override
	public double[] getBestSolution(){
		return population.getMaxIndividual().getPhenotype();
	}

	@Override
	public double getBestFitness(){
		return population.getMaxIndividual().getFitness();
	}

	@Override
	public void logResults(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		new File("logs/").mkdirs();
		String filename = "logs/log_" + (myConfig.getName().length() == 0 ? "" : myConfig.getName() + "_") + dateFormat.format(date) + ".txt";
		try (PrintWriter out = new PrintWriter(filename)) {
            out.println("Date: " + (new SimpleDateFormat("yyyy/MM/dd, HH:mm:ss")).format(date));
			out.println(getLogString());
		}
		catch (Exception e){
			TheOptimizers.println("Could not write results to file. Error message:");
			TheOptimizers.println(e.getMessage());
		}
	}

	@Override
    public String getLogString(){
	    String s = "";
        s += ("Evaluation class: " + TheOptimizers.evaluation_.getClass().getName()) + "\n";
        s += ("Evolution cycles: " + number_cycles) + "\n";
        s += ("Seed (randomness): " + TheOptimizers.rnd_seed) + "\n";
        s += ("Best fitness: " + getBestFitness()) + "\n";
        s += ("Best solution: ") + "\n";
        double[] best_solution = getBestSolution();
        for(int i=0;i<best_solution.length;i++){
            s += ("\t("+i+") "+best_solution[i]) + "\n";
        }
        s += ("\n"+myConfig.toString()) + "\n";
        return s;
    }

    @Override
	public void addTracer(Tracer tracer){
		this.tracer = tracer;
	}

	@Override
	public void writeTraceFiles(){
		tracer.writeOut();
	}

	@Override
	public String getExtraDescription() {
		return population.getDescription();
	}
}
