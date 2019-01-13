package mutation;

import individuals.GeneTypes;
import individuals.Individual;
import algorithm.player59;

/**
 * Created by phlippe on 07.09.18.
 */
public class GaussianMutation extends Mutation
{

	private double prob;
	private double variance;

	public GaussianMutation(double variance){
		this(variance, 1.0);
	}

	public GaussianMutation(double variance, double prob){
		super();
		this.variance = variance;
		this.prob = prob;
	}

	public GaussianMutation(double variance, GeneTypes[] geneTypes){
		this(variance, 1.0, geneTypes);
	}

	public GaussianMutation(double variance, double prob, GeneTypes[] geneTypes){
		super(geneTypes);
		this.variance = variance;
		this.prob = prob;
	}

	@Override
	void applyMutation(double[] genes, Individual individual)
	{
		for (int i = 0; i < genes.length; i++)
		{
			if (player59.rnd_.nextDouble() <= this.prob)
			{
				double change = player59.rnd_.nextGaussian() * variance;
				genes[i] += change;
			}
		}
	}

	@Override
	public String getMutationDescription()
	{
		String s = "";
		s += "Mutation algorithm: Adding a small value from a gaussian distribution to every gene.\n";
		s += "Parameters: \n";
		s += "\tσ = " + variance + "\n";
		s += "\tprob = " + prob + "\n";
		return s;
	}

}
