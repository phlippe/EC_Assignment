package configuration;

import individuals.BoundRepresentation;
import individuals.GeneTypes;
import individuals.GenoRepresentation;
import mutation.GaussianMutation;
import mutation.Mutation;
import recombination.RandomRecombination;
import recombination.Recombination;
import selection.ParentFitnessSelection;
import selection.ParentSelection;
import selection.SurvivorFitnessSelection;
import selection.SurvivorSelection;

import java.util.ArrayList;

/**
 * Created by phlippe on 08.09.18.
 */
public class ExampleConfig extends Configuration
{

	private int population_size = 100;
	private int number_recombinations = 10;
	private int parent_arity = 2;
	private double[] variances = {0.05};


	public ExampleConfig(){
		super();
		init();
	}

	public ExampleConfig(int population_size, int number_recombinations, int parent_arity){
		this.population_size = population_size;
		this.number_recombinations = number_recombinations;
		this.parent_arity = parent_arity;
		init();
	}

	public ExampleConfig(int population_size, int number_recombinations, int parent_arity, double[] variances){
		this.population_size = population_size;
		this.number_recombinations = number_recombinations;
		this.parent_arity = parent_arity;
		this.variances = variances;
		init();
	}

	public ExampleConfig(int population_size, int number_recombinations, int parent_arity, double v){
		this.population_size = population_size;
		this.number_recombinations = number_recombinations;
		this.parent_arity = parent_arity;
		variances = new double[1];
		variances[0] = v;
		init();
	}

	@Override
	protected GenoRepresentation createRepresentation()
	{
		GenoRepresentation genoRepresentation = new BoundRepresentation(10, new int[0], new GeneTypes[0], -5, 5);
		return genoRepresentation;
	}

	@Override
	protected ArrayList<Mutation> createMutationOperators()
	{
		ArrayList<Mutation> mutations = new ArrayList<>();
		for(double v: variances){
			mutations.add(new GaussianMutation(v));
		}
		return mutations;
	}

	@Override
	protected Recombination createRecombination()
	{
		Recombination recombination = new RandomRecombination();
		return recombination;
	}

	@Override
	protected ParentSelection createParentSelection()
	{
		ParentSelection parentSelection = new ParentFitnessSelection();
		return parentSelection;
	}

	@Override
	protected SurvivorSelection createSurvivorSelection()
	{
		SurvivorSelection survivorSelection = new SurvivorFitnessSelection();
		return survivorSelection;
	}

	@Override
	public int getPopulationSize()
	{
		return population_size;
	}

	@Override
	public int getNumberOfRecombinations()
	{
		return number_recombinations;
	}

	@Override
	public int getParentArity()
	{
		return parent_arity;
	}
}
