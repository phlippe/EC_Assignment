package algorithm;

import configuration.Configuration;
import individuals.GeneTypes;
import individuals.GenoRepresentation;
import individuals.Individual;
import individuals.Population;
import initialization.RandomGenoInitializer;
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
public class EvolutionaryCycle
{

	private Population population;
	private GenoRepresentation genoRepresentation;
	private SurvivorSelection survivorSelection;
	private ParentSelection parentSelection;
	private Recombination recombination;
	private ArrayList<Mutation> mutations;
	private Configuration myConfig;
	private int number_cycles;

	public EvolutionaryCycle(Configuration myConfig){
		this.myConfig = myConfig;
		population = new Population(myConfig.getPopulationSize());
		genoRepresentation = myConfig.getRepresentation();
		initialize();
		survivorSelection = myConfig.getSurvivorSelection();
		parentSelection = myConfig.getParentSelection();
		recombination = myConfig.getRecombination();
		mutations = myConfig.getMutationOperators();
		number_cycles = 0;
	}

	private void initialize(){
		population.initialize(genoRepresentation, new RandomGenoInitializer());
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
	}

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
	}

	public double[] getBestSolution(){
		return population.getMaxIndividual().getPhenotype();
	}

	public double getBestFitness(){
		return population.getMaxIndividual().getFitness();
	}

	public void logResults(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		new File("logs/").mkdirs();
		String filename = "logs/log_" + (myConfig.getName().length() == 0 ? "" : myConfig.getName() + "_") + dateFormat.format(date) + ".txt";
		try (PrintWriter out = new PrintWriter(filename)) {
			out.println("Date: " + (new SimpleDateFormat("yyyy/MM/dd, HH:mm:ss")).format(date));
			out.println("Evaluation class: " + TheOptimizers.evaluation_.getClass().getName());
			out.println("Evolution cycles: " + number_cycles);
			out.println("Seed (randomness): " + TheOptimizers.rnd_seed);
			out.println("Best fitness: " + getBestFitness());
			out.println("Best solution: ");
			double[] best_solution = getBestSolution();
			for(int i=0;i<best_solution.length;i++){
				out.println("\t("+i+") "+best_solution[i]);
			}
			out.println("\n"+myConfig.toString());
		}
		catch (Exception e){
			System.out.println("Could not write results to file. Error message:");
			System.out.println(e.getMessage());
		}
	}
}
