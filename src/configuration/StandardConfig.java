package configuration;

import individuals.BoundRepresentation;
import individuals.GeneTypes;
import individuals.GenoRepresentation;
import initialization.GenoInitializer;
import initialization.RandomGenoInitializer;
import mutation.*;
import recombination.*;
import selection.*;

import java.util.ArrayList;

/**
 * Created by phlippe on 08.09.18.
 */
public class StandardConfig extends Configuration
{

	private ConfigParams params;

	public StandardConfig(ConfigParams params){
		super();
		this.params = params;
		setName(this.params.getName());
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
		EvovleMutation evMut = new EvovleMutation(multi, params.getPopulationSize(), params.getMutationMultiSigmaFactor());
		ResetMutation resMut = new ResetMutation(multi, params.getMutationMultiSigmaInit());
		mutations.add(new CombinedMutation(evMut, resMut, 1 - params.getMutationResetProbability()));
		mutations.add(new MultiSigma(genes));

		return mutations;
	}

	@Override
	protected Recombination createRecombination()
	{
		Recombination recombination;
		switch (params.getRecombinationType()){
			case BLEND_CROSSOVER:
				recombination = new BlendCrossover(params.getRecombinationBlendAlpha());
				break;
			case BLEND_RANDOM_CROSSOVER:
				recombination = new BlendRandomRecombination(params.getRecombinationBlendRandomSigma());
				break;
			case RANDOM_CROSSOVER:
				recombination = new RandomRecombination();
				break;
			case SIMULATED_BINARY_CROSSOVER:
				recombination = new SimulatedBinaryCrossover(params.getRecombinationSBCEta());
				break;
			case WHOLE_ARITHMETIC_CROSSOVER:
				recombination = new WholeArithmeticRecombination(params.getRecombinationWACAlpha());
				break;
			default:
				recombination = new RandomRecombination();
				break;
		}
		return recombination;
	}

	@Override
	protected ParentSelection createParentSelection()
	{
		ParentSelectionStochastic parentSelectionStochastic = null;
		switch(params.getParentSelectionStochasticType()){
			case ROULETTE:
				parentSelectionStochastic = new ParentSelectionStochasticRoulette();
				break;
			case UNIVERSAL:
				parentSelectionStochastic = new ParentSelectionStochasticUniversal();
				break;
		}
		ParentSelection parentSelection = null;
		switch(params.getParentSelectionType()){
			case FITNESS_PROPORTIONAL:
				parentSelection = new ParentFitnessSelection(parentSelectionStochastic);
				break;
			case SIGMAL_SCALING:
				parentSelection = new ParentSigmaScalingSelection(parentSelectionStochastic, params.getParentSigmaScalingS());
				break;
			case RANKING_SELECTION:
				parentSelection = new RankingSelection(parentSelectionStochastic, params.getParentRankingS(), params.getParentRankingType());
				break;
			case TOURNAMENT_SELECTION:
				parentSelection = new TournamentSelection(parentSelectionStochastic, params.getParentTournamentSize());
				break;
		}
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
		a.add(new RandomGenoInitializer(params.getMutationMultiSigmaInit(), params.getMutationMultiSigmaInit()));
		return a;
	}

	@Override
	public int getPopulationSize()
	{
		return params.getPopulationSize();
	}

	@Override
	public int getNumberOfRecombinations()
	{
		return params.getNumberRecombinations();
	}

	@Override
	public int getParentArity()
	{
		return params.getParentArity();
	}
}
