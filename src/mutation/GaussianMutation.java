package mutation;

import individuals.GeneTypes;
import individuals.Individual;
import algorithm.TheOptimizers;

/**
 * Created by phlippe on 07.09.18.
 */
public class GaussianMutation extends Mutation
{

	private double variance;

	public GaussianMutation(double variance){
		super();
		this.variance = variance;
	}

	public GaussianMutation(double variance, GeneTypes[] geneTypes){
		super(geneTypes);
		this.variance = variance;
	}

	@Override
	void applyMutation(double[] genes)
	{
		for (int i = 0; i < genes.length; i++)
		{
			double change = TheOptimizers.rnd_.nextGaussian() * variance;
			genes[i] += change;
		}
	}

	@Override
	public String getMutationDescription()
	{
		String s = "";
		s += "Mutation algorithm: Adding a small value from a gaussian distribution to every gene.\n";
		s += "Parameters: \n";
		s += "\tσ = " + variance + "\n";
		return s;
	}

}
