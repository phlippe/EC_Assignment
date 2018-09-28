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
		tracer.initialize();
		tracer.addTraceFile(TraceTags.MAX_FITNESS);
		tracer.addTraceFile(TraceTags.MEAN_FITNESS);
		tracer.addTraceFile(TraceTags.MIN_FITNESS);
		tracer.addTraceFile(TraceTags.MEAN_DISTANCE);
		tracer.addTraceFile(TraceTags.MEAN_POSITION);
	}

	@Override
	public void run_single_cycle(){
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
		// 5. Survivor selection
		survivorSelection.selectSurvivors(population, children);
		population.increaseAge();
		number_cycles++;
		tracePopulation();
	}

	private void tracePopulation(){
		tracer.addTraceContent(TraceTags.MAX_FITNESS, population.getMaxIndividual().getFitness());
		tracer.addTraceContent(TraceTags.MIN_FITNESS, population.getMinIndividual().getFitness());
		tracer.addTraceContent(TraceTags.MEAN_FITNESS, population.getMeanFitness());
		tracer.addTraceContent(TraceTags.MEAN_POSITION, population.getMeanPosition());
		tracer.addTraceContent(TraceTags.MEAN_DISTANCE, population.getMeanDistance(population.getMeanPosition()));
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
}
