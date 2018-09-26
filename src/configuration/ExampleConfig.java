package configuration;

import individuals.BoundRepresentation;
import individuals.GeneTypes;
import individuals.GenoRepresentation;
import initialization.GenoInitializer;
import initialization.RandomGenoInitializer;
import mutation.EvovleMutation;
import mutation.GaussianMutation;
import mutation.MultiSigma;
import mutation.Mutation;
import recombination.*;
import selection.*;

import java.util.ArrayList;

/**
 * Created by phlippe on 08.09.18.
 */
public class ExampleConfig extends Configuration
{

	private int population_size = 100;
	private int number_recombinations = 10;
	private int parent_arity = 2;
	private double init_variance = 0.5;
	private double sigvariance;


	public ExampleConfig(){
		super();
		init();
	}

	public ExampleConfig(int population_size, int number_recombinations, int parent_arity) {
		this(population_size, number_recombinations, parent_arity, "");
	}

	public ExampleConfig(int population_size, int number_recombinations, int parent_arity, String name){
		this.population_size = population_size;
		this.number_recombinations = number_recombinations;
		this.parent_arity = parent_arity;
		setName(name);
		init();
	}

	public ExampleConfig(int population_size, int number_recombinations, int parent_arity, double init_variance){
		this(population_size, number_recombinations, parent_arity, init_variance, "");
	}

	public ExampleConfig(int population_size, int number_recombinations, int parent_arity, double init_variance, String name) {
		this.population_size = population_size;
		this.number_recombinations = number_recombinations;
		this.parent_arity = parent_arity;
		this.init_variance = init_variance;
		setName(name);
		init();
	}

	@Override
	protected GenoRepresentation createRepresentation()
	{
		int[] num = {10};
		GeneTypes[] gene = {GeneTypes.MULTI_SIGMA};
		GenoRepresentation genoRepresentation = new BoundRepresentation(10, num, gene, -5, 5);
		return genoRepresentation;
	}

	@Override
	protected ArrayList<Mutation> createMutationOperators()
	{
		GeneTypes[] multi = {GeneTypes.MULTI_SIGMA};
		GeneTypes[] genes = {GeneTypes.OPT_GENES};
		ArrayList<Mutation> mutations = new ArrayList<>();
		mutations.add(new EvovleMutation(multi, population_size, 1));
		mutations.add(new MultiSigma(genes));

		return mutations;
	}

	@Override
	protected Recombination createRecombination()
	{
		Recombination recombination = new BlendRandomRecombination(0.0);
		return recombination;
	}

	@Override
	protected ParentSelection createParentSelection()
	{
		ParentSelectionStochastic parentSelectionStochastic = new ParentSelectionStochasticUniversal();
		ParentSelection parentSelection = new ParentSigmaScalingSelection(parentSelectionStochastic, 2);
		return parentSelection;
	}

	@Override
	protected SurvivorSelection createSurvivorSelection()
	{
		SurvivorSelection survivorSelection = new SurvivorFitnessSelection();
		return survivorSelection;
	}

	@Override
	protected GenoInitializer createGenoInitializer()
	{
		return new RandomGenoInitializer();
	}

	@Override
	protected ArrayList<GenoInitializer> createAddParamsInitializer()
	{
		ArrayList<GenoInitializer> a = new ArrayList<>();
		a.add(new RandomGenoInitializer(init_variance, init_variance));
		return a;
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
